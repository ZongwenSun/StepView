package cn.bit.szw.stepview;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import cn.bit.szw.widget.StepView;

/**
 * Created by szw on 16/3/24.
 */
public class MainActivity extends Activity {
    StepView mStepView;
    String[] lables = {"获取","使用","支付","完成"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mStepView = (StepView)findViewById(R.id.stepview);
        List<String> lables = new ArrayList<>();
        lables.add("获取");
        lables.add("获取资格");
        lables.add("获取");
        lables.add("订单完成");
        mStepView.setStepText(lables);
        mStepView.setCurrentStep(2);
        mStepView.setDrawable(getResources().getDrawable(R.drawable.bg_cur_marker));
        mStepView.setDrawableMargin(12);
        mStepView.setVerticalSpace(48);
    }



}
