package eu.codlab.cyphersend.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import eu.codlab.cyphersend.R;

/**
 * Created by kevinleperf on 19/03/2014.
 */
public class RobotoTextview extends TextView {

    public RobotoTextview(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode()) {
            return;
        }
        /*
        <resources>
            <declare-styleable name="TypefacedTextView">
                <attr name="typeface" format="string" />
            </declare-styleable>
        </resources>
         */
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.RobotoTextView);
        String fontName = styledAttrs.getString(R.styleable.RobotoTextView_typeface);
        styledAttrs.recycle();

        if ("regular".equals(fontName)) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
            setTypeface(typeface);
        }else{
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-thin.ttf");
            setTypeface(typeface);
        }
    }

}