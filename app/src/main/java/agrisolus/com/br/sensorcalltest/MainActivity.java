package agrisolus.com.br.sensorcalltest;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText et_endereco;
    private Button bt_enviar;
    private TextView tv_retorno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_enviar = (Button) findViewById(R.id.bt_enviar);
        et_endereco = (EditText) findViewById(R.id.et_endereco);
        tv_retorno = (TextView) findViewById(R.id.tv_retorno);

        bt_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requisicao(et_endereco.getText().toString());
            }
        });

    }

    private void requisicao(String endereco) {
        try {

            tv_retorno.setText(POSTRequest(endereco));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * POSTRequest
     * @param URL
     * @return
     */
    private String POSTRequest(String URL) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
            HttpConnectionParams.setSoTimeout(httpParameters, 30000);

            HttpPost httpPost = new HttpPost(URL);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpResponse response = httpClient.execute(httpPost);

            String responseString = EntityUtils.toString(response.getEntity());
            httpClient.getConnectionManager().shutdown();

            return responseString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
