package cn.twesix.chat;

import android.os.Handler;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class HTTP
{

    static void get(final String request_url, final Callback callback)
    {
        final String url = "http://10.0.2.2:10000/" + request_url;
        final Handler handler = new Handler();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final String response = getSync(url);
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    private static String getStringFromInputStream(InputStream is)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String state = null;
        // 模板代码 必须熟练
        byte[] buffer = new byte[1024];
        int len = -1;
        try
        {
            while ((len = is.read(buffer)) != -1)
            {
                os.write(buffer, 0, len);
            }
            is.close();
            state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
            os.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return state;
    }

    @Nullable
    static String getSync(String request_url)
    {
        HttpURLConnection conn = null;
        try
        {
            URL url = new URL(request_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(10000);

            int response_code = conn.getResponseCode();
            if(response_code == 200)
            {
                InputStream is = conn.getInputStream();
                String response = getStringFromInputStream(is);
                return response;
            }
            else
            {
                System.out.println(conn.getResponseMessage());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(conn != null)
            {
                conn.disconnect();
            }
        }
        return null;
    }
}
