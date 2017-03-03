package course.palienko.modernartui_1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;



/*
    Sorry for my bad English in comments, I am not native speaker.

    If you want change the URL or Dialog text you can do it in the strings.xml

    The idea of the code is an array of colored rectangles (TextViews), except white rectangle.
    The rectangles are colored by random colors in OnCreate. Also you can use the menu item for doing this.
*/

public class MainActivity extends Activity {

    private  SeekBar sbSlider;
    private TextView tvRect1;
    private int colorCode;
    private final int RECTANGLE_COUNT = 5;
    private final int COLOR_COUNT = 5;
    private final Integer[] aColors = {R.color.mRed, R.color.mBlue, R.color.mGreen, R.color.mWhite, R.color.mYellow};
    private ArrayList<Integer> alColors = new ArrayList<Integer>(Arrays.asList(aColors));

    // we need save initial background colors of rectangles
    // so this is simple class for it
    protected  class ViewAndColor {
        TextView tv;                        // rectangle
        int col;                            // initial background color of rectangle
        ViewAndColor(TextView t, int c) { tv = t; col = c;}
    }
    private ArrayList<ViewAndColor>  aRects = new ArrayList<ViewAndColor>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sbSlider = (SeekBar) findViewById(R.id.seekBar);

        paint_rectangles();

        //colorCode = ((ColorDrawable) tvRect1.getBackground()).getColor();

        sbSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int col;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                for( int i = 0;  i < aRects.size(); i++) {
                    ViewAndColor tmp = aRects.get(i);
                    int col = recalcColor(tmp.col,progress);
                    (tmp.tv).setBackgroundColor(col);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    protected void paint_rectangles() {
        Collections.shuffle(alColors);
        aRects.clear();

        for( int i = 1;  i <= RECTANGLE_COUNT; i++) {
            int id = getResources().getIdentifier("tv"+i, "id", getPackageName());

            TextView tmp = (TextView) findViewById(id);
            int col = getResources().getColor(alColors.get((i-1)%COLOR_COUNT));
            tmp.setBackgroundColor(col);
            // gradient does not need for WHITE color
            if( col != Color.WHITE) {
                aRects.add(new ViewAndColor(tmp,col));
            }
        }
        sbSlider.setProgress(0);
    }
    protected int recalcColor(int cc, int progress)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(cc,hsv);
        hsv[0]=hsv[0]+progress;
        hsv[0]=hsv[0]%360;                //confines hue to values:[0,360]
        int col = Color.HSVToColor(Color.alpha(cc),hsv);
        return col;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.more_information:
                AlertDialogFragment.newInstance().show(getFragmentManager(), "Alert");
                return true;
            case R.id.repaint_rects:
                paint_rectangles();

                return true;
            default:
                return false;
        }
    }


    // Class that creates the AlertDialog
    public static class AlertDialogFragment extends DialogFragment {

        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        // Build AlertDialog using AlertDialog.Builder
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // make dialog beautiful
            LinearLayout ll = new LinearLayout(getActivity());
            TextView tv = new TextView(getActivity());
            tv.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(5,5,5,5);
            ll.addView(tv,lp);

            tv.setText(R.string.dialog_text);
            return new AlertDialog.Builder(getActivity())

                    //.setMessage(R.string.dialog_text)
                    .setTitle(R.string.dialog_title)
                    // User can dismiss dialog by hitting back button
                    .setCancelable(true)

                    .setView(ll)
                    // Set up No Button
                    .setNegativeButton(R.string.dialog_btnNo,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    ; // nothing to do
                                }
                            })

                    // Set up Yes Button
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog, int id) {
                                    ((MainActivity) getActivity()).goto_uri();
                                }
                            }).create();
        }
    }

    private void goto_uri() {
        Intent i = new Intent(Intent.ACTION_VIEW);

        i.setData(Uri.parse(getResources().getString(R.string.uri_coursera)));
        startActivity(i);
    }
}
