package com.hfad.gamo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VolleyForHttpMethod {

    private RequestQueue queue;

    public VolleyForHttpMethod(RequestQueue queue) {
        this.queue = queue;
    }

    public void postJSONObjectString(@NonNull final JSONObject Body,@NonNull String url, @Nullable Response.Listener<String> listener) {

        StringRequest request;

        if(listener == null) {
            request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VolleyResponse", "Response is good");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                    Log.i("VolleyError", "Volley Error in receive");
                }
            }
            ) {
                @Override
                public byte[] getBody() {
                    return Body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
        } else {
            request = new StringRequest(Request.Method.POST, url, listener, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                    Log.i("VolleyError", "Volley Error in receive");
                }
            }
            ) {
                @Override
                public byte[] getBody() {
                    return Body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
        }

        request.setShouldCache(false);
        queue.add(request);
    }

    public void postJSONObjectJSONArray(@NonNull final JSONObject Body, @NonNull String url, @Nullable final Response.Listener<JSONArray> listener, @Nullable Response.ErrorListener ErrorListener) {

        StringRequest request;

        if(listener == null && ErrorListener == null) {
            request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VolleyResponse", "Response is good");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                    Log.i("VolleyError", "Volley Error in receive");
                }
            }
            ) {
                @Override
                public byte[] getBody() {
                    return Body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
        } else if(listener != null && ErrorListener == null) {
            request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        listener.onResponse(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i("VolleyResponse", "Response is good");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                    Log.i("VolleyError", "Volley Error in receive");
                }
            }) {
                @Override
                public byte[] getBody() {
                    return Body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
        } else if(listener == null && ErrorListener != null) {
            request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VolleyResponse", "Response is good");
                }
            }, ErrorListener) {
                @Override
                public byte[] getBody() {
                    return Body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
        } else {
            request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        listener.onResponse(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i("VolleyResponse", "Response is good");
                }
            }, ErrorListener) {
                @Override
                public byte[] getBody() {
                    return Body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
        }

        request.setShouldCache(false);
        queue.add(request);
    }

    public void getString(@NonNull String url, @Nullable Response.Listener<String> listener) {

        StringRequest request;

        if(listener == null) {
            request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VolleyResponse", "Response is good");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                    Log.i("VolleyError", "Volley Error in receive");
                }
            });
        } else {
            request = new StringRequest(Request.Method.GET, url, listener, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                    Log.i("VolleyError", "Volley Error in receive");
                }
            }
            );
        }

        request.setShouldCache(false);
        queue.add(request);
    }

    public void getJSONArray(@NonNull String url, @Nullable Response.Listener<JSONArray> listener) {

        JsonArrayRequest request;

        if(listener == null) {
            request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i("VolleyResponse", "Response is good");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                    Log.i("VolleyError", "Volley Error in receive");
                }
            });
        } else {
            request = new JsonArrayRequest(url, listener, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                    Log.i("VolleyError", "Volley Error in receive");
                }
            });
        }

        request.setShouldCache(false);
        queue.add(request);
    }

    /*public void get(final HashMap<String, Object> Body, String url, Response.Listener<String> listener) {
        StringRequest request;
        if (Body == null) {
            request = new StringRequest(Request.Method.GET, url, listener, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                    Log.i("VolleyError", "Volley Error in receive");
                }
            }
            );
        } else {
            request = new StringRequest(Request.Method.GET, url, listener, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                    Log.i("VolleyError", "Volley Error in receive");
                }
            }
            ) {
                @Override
                public byte[] getBody() {
                    *//*HashMap<String, Object> params = new HashMap<>();
                    params.put("user_id" , "jy1129000");
                    params.put("user_major" , "컴퓨터공학과");
                    params.put("auth_level" , "1");*//*
                    return new JSONObject(Body).toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
        }

        request.setShouldCache(false);
        queue.add(request);
    }*/

}