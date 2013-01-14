package jp.classmethod.android.sample.lrucache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;

/**
 * {@link Bitmap} を非同期で読み込む {@link AsyncTaskLoader}.
 */
public class ImageLoader extends AsyncTaskLoader<Bitmap> {

    /** 対象のアイテム. */
    public ImageItem item;

    /**
     * コンストラクタ.
     * @param context {@link Context}
     * @param item {@link ImageItem}
     */
    public ImageLoader(Context context, ImageItem item) {
        super(context);
        this.item = item;
    }

    @Override
    public Bitmap loadInBackground() {
        return BitmapFactory.decodeResource(getContext().getResources(), R.drawable.item);
    }

}
