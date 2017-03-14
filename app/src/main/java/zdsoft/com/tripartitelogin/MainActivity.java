package zdsoft.com.tripartitelogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private TextView qqlogin;

    private Tencent mTencent;
    private String APP_ID = "222222";
    private IUiListener loginListener;
    private String SCOPE = "all";

    /*
    *   其中，mTencent是必须要用到的一个对象，qq的登录分享功能等大部分都封装在了这个类里边。

    APP_ID，是你创建应用时的appid，这个id必须跟你的清单文件里的 data android:scheme="222222"节点的值是一样的。

    loginListener，是一个回调接口，在你登录或分享成功后会执行该接口。

    SCOPE，应用需要获得哪些API的权限,由“,”分隔。例如:SCOPE = “get_user_info,add_t”;所有权限用“all”
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qqlogin= (TextView) findViewById(R.id.qqlogin);

        qqlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qqLogin(v);
            }
        });
    }



    public void qqLogin(View view) {
        initQqLogin();
        mTencent.login(this, SCOPE, loginListener);
    }

    //初始化QQ登录分享的需要的资源
    private void initQqLogin(){
        mTencent =  Tencent.createInstance(APP_ID, this);
        //创建QQ登录回调接口
        loginListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                //登录成功后回调该方法
                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                if(o==null){
                    return;
                }
                try{
                    JSONObject jo = (JSONObject) o;
                    Log.e("JO:",jo.toString());
                    int ret = jo.getInt("ret");
                    String nickName = jo.getString("nickname");
                    String gender = jo.getString("gender");
                    Toast.makeText(MainActivity.this, "你好，" + nickName,Toast.LENGTH_LONG).show();
                }catch(Exception e){
                    e.printStackTrace();
                }

                JSONObject jo = (JSONObject) o;
                Log.e("COMPLETE:", jo.toString());
                String openID;
                try {
                    openID = jo.getString("openid");
                    String accessToken = jo.getString("access_token");
                    String expires = jo.getString("expires_in");
                    mTencent.setOpenId(openID);
                    mTencent.setAccessToken(accessToken, expires);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(UiError uiError) {
                //登录失败后回调该方法
                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                Log.e("LoginError:", uiError.toString());
            }

            @Override
            public void onCancel() {
                //取消登录后回调该方法
                Toast.makeText(MainActivity.this, "取消登录", Toast.LENGTH_SHORT).show();
            }
        };
    }



    //登录之后回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//  官方文档上面的是错误的
//        if(requestCode == Constants.REQUEST_API) {
//            if(resultCode == Constants.RESULT_LOGIN) {
//                mTencent.handleLoginData(data, loginListener);
//            }
//  resultCode 是log出来的，官方文档里给的那个属性是没有的

        if (requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == -1) {
                Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
                Tencent.handleResultData(data, loginListener);
                UserInfo info = new UserInfo(this, mTencent.getQQToken());
                info.getUserInfo(loginListener);
            }
        }
    }

}
