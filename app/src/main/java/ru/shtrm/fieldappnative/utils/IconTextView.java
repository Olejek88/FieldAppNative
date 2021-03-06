package ru.shtrm.fieldappnative.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;

public class IconTextView extends android.support.v7.widget.AppCompatTextView {

    private Context context;

    public IconTextView(Context context) {
        super(context);
        this.context = context;
        createView();
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        createView();
    }

    private void createView(){
        setGravity(Gravity.CENTER);
        setTypeface(Typeface.createFromAsset(context.getAssets(),"FontAwesomeSolid.otf"));
        //setTypeface(Typeface.createFromAsset(context.getAssets(),"FontAwesomeRegular.otf"));
    }
}