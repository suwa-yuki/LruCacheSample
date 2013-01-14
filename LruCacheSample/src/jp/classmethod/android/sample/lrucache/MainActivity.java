package jp.classmethod.android.sample.lrucache;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * GridView を表示する {@link Activity}.
 */
public class MainActivity extends FragmentActivity {

    /** ログ出力用のタグ. */
    private static final String TAG = MainActivity.class.getSimpleName();

    /** メモリキャッシュクラス. */
    private LruCache<String, Bitmap> mLruCache;
    /** {@link GridView}. */
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGridView = new GridView(this);
        mGridView.setNumColumns(4);
        setContentView(mGridView);

        // LruCache のインスタンス化
        int maxSize = 10 * 1024 * 1024;
        mLruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        // Adapter の作成とアイテムの追加
        ImageAdapter adapter = new ImageAdapter(this);
        mGridView.setAdapter(adapter);
        for (int i = 0; i < 50; i++) {
            ImageItem item = new ImageItem();
            item.key = "item" + String.valueOf(i);
            adapter.add(item);
        }

        // onScrollListener の実装
        mGridView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    // スクロールが止まったときに読み込む
                    loadBitmap();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        loadBitmap();
    }

    /**
     * 画像を読み込む.
     */
    private void loadBitmap() {
        // 現在の表示されているアイテムのみリクエストする
        ImageAdapter adapter = (ImageAdapter) mGridView.getAdapter();
        int first = mGridView.getFirstVisiblePosition();
        int count = mGridView.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageItem item = adapter.getItem(i + first);
            // キャッシュの存在確認
            Bitmap bitmap = mLruCache.get(item.key);
            if (bitmap != null) {
                // キャッシュに存在
                Log.i(TAG, "キャッシュあり=" + item.key);
                setBitmap(item);
                mGridView.invalidateViews();
            } else {
                // キャッシュになし
                Log.i(TAG, "キャッシュなし=" + item.key);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", item);
                getSupportLoaderManager().initLoader(i, bundle, callbacks);
            }
        }
    }

    /**
     * アイテムの View に Bitmap をセットする.
     * @param item
     */
    private void setBitmap(ImageItem item) {
        ImageView view = (ImageView) mGridView.findViewWithTag(item);
        if (view != null) {
            view.setImageBitmap(item.bitmap);
            mGridView.invalidateViews();
        }
    }

    /**
     * ImageLoader のコールバック.
     */
    private LoaderCallbacks<Bitmap> callbacks = new LoaderCallbacks<Bitmap>() {
        @Override
        public Loader<Bitmap> onCreateLoader(int i, Bundle bundle) {
            ImageItem item = (ImageItem) bundle.getSerializable("item");
            ImageLoader loader = new ImageLoader(getApplicationContext(), item);
            loader.forceLoad();
            return loader;
        }
        @Override
        public void onLoadFinished(Loader<Bitmap> loader, Bitmap bitmap) {
            int id = loader.getId();
            getSupportLoaderManager().destroyLoader(id);
            // メモリキャッシュに登録する
            ImageItem item = ((ImageLoader) loader).item;
            Log.i(TAG, "キャッシュに登録=" + item.key);
            item.bitmap = bitmap;
            mLruCache.put(item.key, bitmap);
            setBitmap(item);
        }
        @Override
        public void onLoaderReset(Loader<Bitmap> loader) {
        }
    };
}
