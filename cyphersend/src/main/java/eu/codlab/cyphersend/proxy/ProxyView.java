package eu.codlab.cyphersend.proxy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import eu.codlab.cyphersend.Application;
import eu.codlab.cyphersend.R;

/**
 * Created by kevinleperf on 14/08/14.
 */
public class ProxyView extends LinearLayout implements View.OnClickListener {
    private Button _button;
    private TextView _status;
    private View _layout;

    private void init(){
        Context context = getContext();

        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.component_proxy, this, false);

        _button = (Button) v.findViewById(R.id.tor_button);
        _button.setOnClickListener(this);

        _status = (TextView) v.findViewById(R.id.tor_status);

        _layout = v.findViewById(R.id.background);

        addView(v);

        onResume();
    }
    public ProxyView(Context context) {
        super(context);
        init();
    }

    public ProxyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ProxyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void onResume() {
        if(Application.getInstance() != null) {
            ProxyController controller = Application.getInstance().getProxyController();
            _status.setText(getContext().getString(controller.getResourceText()));

            _button.setText(getContext().getString(controller.getStatusText()));

            _button.setVisibility(controller.isOrbotRunning() ? View.GONE : View.VISIBLE);

            _layout.setBackgroundResource(controller.getBackgroundResource());
        }
    }

    @Override
    public void onClick(View view) {
        if(Application.getInstance() != null) {
            ProxyController controller = Application.getInstance().getProxyController();
            if (getContext() instanceof Activity) {
                if (!controller.isOrbotInstalled()) {
                    controller.promptToInstall((Activity) getContext());
                } else if (!controller.isOrbotRunning()) {
                    controller.requestOrbotStart((Activity) getContext());
                }
            }
        }
    }
}
