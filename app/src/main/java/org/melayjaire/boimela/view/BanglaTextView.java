package org.melayjaire.boimela.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.utils.Utilities;

public class BanglaTextView extends TextView {

    TypedArray typedArray;
    Typeface myTypeface;
    String banglaText;
    String fontName;

    public BanglaTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public BanglaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BanglaTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setBanglaText(String banglaText) {
        setBanglaSupportedText(banglaText);
        if (typedArray != null) {
            typedArray.recycle();
        }
    }

    public void appendBanglaText(String banglaText) {
        append(Utilities.getBanglaSpannableString(banglaText, getContext()));
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        if (attrs != null) {
            try {
                typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BanglaTextView);
                fontName = typedArray.getString(R.styleable.BanglaTextView_font);
                myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                banglaText = typedArray.getString(R.styleable.BanglaTextView_banglaText);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (fontName != null) {
                setUpTypeFace(myTypeface);
            }

            setBanglaSupportedText(banglaText);

            typedArray.recycle();
        }
    }

    private void setUpTypeFace(Typeface myTypeface) {
        if (!Utilities.isBanglaAvailable() & Utilities.isBuildAboveHoneyComb()) {
            //nothing just add default font
        } else {
            setTypeface(myTypeface);
        }
    }

    private void setBanglaSupportedText(String banglaText) {
        if (banglaText != null) {
            setText(Utilities.getBanglaSpannableString(banglaText, getContext()));
        }
    }

}
