package agrisolus.com.br.sensorcalltest;

import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{

    private EditText et_endereco;
    private EditText et_ssid;
    private EditText et_pwd;
    private Button bt_enviar;
    private Button bt_config;
    private Button bt_crono;
    private Chronometer cr;
    private TextView tv_retorno;

    private final ArrayList<String> translateTable = new ArrayList<>(); //"!\"#$%&' ()*+,-./:;<=>?@[\\]^_`{|}~";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int i = 0; i < 16; i++)
        {
            String Aux = String.format("%%%x",i+32);
            translateTable.add(Aux);
        }
        for(int i = 0; i < 7; i++)
        {
            String Aux = String.format("%%%x",i+58);
            translateTable.add(Aux);
        }
        for(int i = 0; i < 6; i++)
        {
            String Aux = String.format("%%%x",i+91);
            translateTable.add(Aux);
        }
        for(int i = 0; i < 4; i++)
        {
            String Aux = String.format("%%%x",i+123);
            translateTable.add(Aux);
        }

        bt_enviar = (Button) findViewById(R.id.bt_enviar);
        bt_config = (Button) findViewById(R.id.bt_send_cfg);
        bt_crono  = (Button) findViewById(R.id.bt_crono);


        et_endereco = (EditText) findViewById(R.id.et_endereco);
        et_ssid = (EditText) findViewById(R.id.et_SSID);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        tv_retorno = (TextView) findViewById(R.id.tv_retorno);
        cr = (Chronometer) findViewById(R.id.chronometer2);

        bt_enviar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                requisicao(et_endereco.getText().toString());
            }
        });

        bt_config.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String Aux = et_endereco.getText() + "cmd=apcfg&ssid=" + et_ssid.getText() + "&password=" + et_pwd.getText();
                String payload = setTranslate(Aux, Aux.length());
                requisicao("/?" + payload);
            }
        });

        bt_crono.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if( bt_crono.getText().toString().equalsIgnoreCase("CRONOMETRAR")   )
                {
                    requisicao(et_endereco.getText() + "/?cmd=rst");
                    cr.setBase(SystemClock.elapsedRealtime());
                    cr.start();
                    bt_crono.setText("Parar");
                }else
                {
                    requisicao(et_endereco.getText().toString());
                    bt_crono.setText("Cronometrar");
                    cr.stop();
                }
            }
        });

        cr.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener()
        {
            @Override
            public void onChronometerTick(Chronometer chronometer)
            {
                if( chronometer.getText().toString().equalsIgnoreCase("01:00"))
                {
                    requisicao(et_endereco.getText().toString());
                    cr.stop();
                    bt_crono.setText("Cronometrar");
                }
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

            HttpGet httpGet = new HttpGet("http://" + URL);
            httpGet.setHeader("USER-AGENT", "Mozilla/5.0");
            httpGet.setHeader("ACCEPT-LANGUAGE", "en-US,en;0.5");


            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpResponse response = httpClient.execute(httpGet);

            String responseString = EntityUtils.toString(response.getEntity());
            httpClient.getConnectionManager().shutdown();

            return responseString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /* Função usada para padronizar caracteres especiais no formato html
       Exemplo:
            IN = "123 456"
            SIZE = 7
            OUT = 123%20456  --> onde %20 corresponde ao caracter "espaço"
     */
    public String setTranslate(String IN, int size)
    {
        String OUT = "";

        for(int i = 0; i < size; i++)
        {
            char c = IN.charAt(i);

            if( (c >= 32 && c < 48) )
            {
                OUT += translateTable.get(c - 32);
            }else if( c >= 58 && c < 65 )
            {
                OUT += translateTable.get(c - 58 + 16);
            }else if( c >= 91 && c < 97 )
            {
                OUT += translateTable.get(c - 91 + 16 + 7);
            }else if( c >= 123 && c < 0x128 )
            {
                OUT += translateTable.get(c - 123 + 16 + 6 + 7);
            }else
                OUT += String.valueOf(c);
        }
        return OUT;
    }
}
