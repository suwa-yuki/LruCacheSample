package jp.classmethod.android.sample.lrucache;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * アイテムのデータ.
 */
public class ImageItem implements Serializable {
    /** シリアルバージョン. */
    private static final long serialVersionUID = 1L;
    /** {@link Bitmap}. */
    public Bitmap bitmap;
    /** キー. */
    public String key;
}
