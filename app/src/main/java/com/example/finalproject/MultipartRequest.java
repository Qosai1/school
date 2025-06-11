package com.example.finalproject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {
    private final String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
    private final String lineEnd = "\r\n";
    private final String twoHyphens = "--";

    private Response.Listener<String> mListener;
    private Response.ErrorListener mErrorListener;
    private Map<String, String> mStringParams = new HashMap<>();
    private Map<String, FileParam> mFileParams = new HashMap<>();

    public MultipartRequest(int method, String url,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    public void addStringParam(String key, String value) {
        mStringParams.put(key, value);
    }

    public void addFileParam(String key, String fileName, byte[] fileData) {
        mFileParams.put(key, new FileParam(fileName, fileData));
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // Add string parameters
            for (Map.Entry<String, String> entry : mStringParams.entrySet()) {
                buildTextPart(dos, entry.getKey(), entry.getValue());
            }

            // Add file parameters
            for (Map.Entry<String, FileParam> entry : mFileParams.entrySet()) {
                buildFilePart(dos, entry.getKey(), entry.getValue());
            }

            // Close multipart form data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            dos.flush();
            dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    private void buildTextPart(DataOutputStream dos, String key, String value) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.writeBytes(value);
        dos.writeBytes(lineEnd);
    }

    private void buildFilePart(DataOutputStream dos, String key, FileParam fileParam) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileParam.fileName + "\"" + lineEnd);
        dos.writeBytes("Content-Type: application/octet-stream" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.write(fileParam.fileData);
        dos.writeBytes(lineEnd);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new VolleyError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        }
    }

    private static class FileParam {
        String fileName;
        byte[] fileData;

        FileParam(String fileName, byte[] fileData) {
            this.fileName = fileName;
            this.fileData = fileData;
        }
    }
}