package com.example.kingwen.speechrecognitiondemo.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kingwen.speechrecognitiondemo.R;
import com.example.kingwen.speechrecognitiondemo.Utils.JsonParser;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {
    //TAG
    private static String TAG=MainActivity.class.getSimpleName();

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private Toast mToast;


    //点击开始录音按钮
    private Button btn_start_record;
    //展示结果的textview
    private TextView tv_showResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initListener();

    }

    private void initListener() {

        btn_start_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 显示听写对话框
                mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
                mIatDialog.setListener(recognizerDialogListener);
                mIatDialog.show();

                showTip("setParam" + getString(R.string.text_begin));

            }
        });


    }


    /**
     * 听写UI监听器
     */
    public RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {

        public void onResult(RecognizerResult results, boolean isLast) {

            Log.e("recognizeListener","onresult");
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
            Log.e("recognizeListener", "onError");
        }

    };


    private void initView() {

        //点击开始识别的按钮
        btn_start_record= (Button) findViewById(R.id.start_record_Mainactivity);
        //用来展示结果的textview
        tv_showResult= (TextView) findViewById(R.id.tv_showRecongnizedResult_Mainactivity );

        //初始化识别对象，可以根据回调消息自定义界面
        mIat=SpeechRecognizer.createRecognizer(MainActivity.this,null);

        setparam();

        //先将我们存储结果的hash表清空
        mIatResults.clear();

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(MainActivity.this, null);

        mToast=Toast.makeText(this, "", Toast.LENGTH_SHORT);


    }

    private void setparam() {
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);

        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

      /*  // 设置音频保存路径，保存音频格式仅为pcm，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/iflytek/wavaudio.pcm");
*/
        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        mIat.setParameter(SpeechConstant.ASR_DWA, "0");

    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        tv_showResult.setText(resultBuffer.toString());

        showTip(resultBuffer.toString());

    }

}
