package com.gachon.moga;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
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

    private final static String TAG = "VolleyForHttpMethod";
    private RequestQueue queue;

    public VolleyForHttpMethod(RequestQueue queue) {
        this.queue = queue;
    }

    public void postJSONObjectString(@Nullable final JSONObject Body,@NonNull String url, @Nullable Response.Listener<String> listener, @Nullable Response.ErrorListener ErrorListener) {

        StringRequest request;

        if (Body != null) {
            if (listener == null && ErrorListener == null) {
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
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
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

            } else if (listener != null && ErrorListener == null) {
                request = new StringRequest(Request.Method.POST, url, listener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                        Log.i("VolleyError", "Volley Error in receive");
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
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

            } else if (listener == null && ErrorListener != null) {
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
                Log.d(TAG, "postJSONObjectString: listener != null, ErrorListener != null");
                request = new StringRequest(Request.Method.POST, url, listener, ErrorListener) {
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
        } else {
            if (listener == null && ErrorListener == null) {
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
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
                    }
                });
            } else if (listener != null && ErrorListener == null) {
                request = new StringRequest(Request.Method.POST, url, listener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                        Log.i("VolleyError", "Volley Error in receive");
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
                    }
                });
            } else if (listener == null && ErrorListener != null) {
                request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VolleyResponse", "Response is good");
                    }
                }, ErrorListener);
            } else {
                Log.d(TAG, "postJSONObjectString: listener != null, ErrorListener != null");
                request = new StringRequest(Request.Method.POST, url, listener, ErrorListener);
            }
        }

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(true);
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

        Log.d(TAG, "postJSONObjectJSONArray: added to queue");
        request.setShouldCache(true);
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

        request.setShouldCache(true);
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

        request.setShouldCache(true);
        queue.add(request);
    }

    public void putJSONObjectString(@NonNull final JSONObject Body, @NonNull String url, @Nullable Response.Listener<String> listener, @Nullable Response.ErrorListener ErrorListener) {
        StringRequest request;

        if (Body != null) {
            if (listener == null && ErrorListener == null) {
                request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VolleyResponse", "Response is good");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                        Log.i("VolleyError", "Volley Error in receive");
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
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

            } else if (listener != null && ErrorListener == null) {
                request = new StringRequest(Request.Method.PUT, url, listener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                        Log.i("VolleyError", "Volley Error in receive");
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
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

            } else if (listener == null && ErrorListener != null) {
                request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
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
                Log.d(TAG, "postJSONObjectString: listener != null, ErrorListener != null");
                request = new StringRequest(Request.Method.PUT, url, listener, ErrorListener) {
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
        } else {
            if (listener == null && ErrorListener == null) {
                request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VolleyResponse", "Response is good");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                        Log.i("VolleyError", "Volley Error in receive");
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
                    }
                });
            } else if (listener != null && ErrorListener == null) {
                request = new StringRequest(Request.Method.PUT, url, listener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                        Log.i("VolleyError", "Volley Error in receive");
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
                    }
                });
            } else if (listener == null && ErrorListener != null) {
                request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VolleyResponse", "Response is good");
                    }
                }, ErrorListener);
            } else {
                Log.d(TAG, "postJSONObjectString: listener != null, ErrorListener != null");
                request = new StringRequest(Request.Method.PUT, url, listener, ErrorListener);
            }
        }

        request.setShouldCache(true);
        queue.add(request);
    }



    public void delete(@Nullable final JSONObject Body,@NonNull String url, @Nullable Response.Listener<String> listener, @Nullable Response.ErrorListener ErrorListener) {

        StringRequest request;

        if (Body != null) {
            if (listener == null && ErrorListener == null) {
                request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VolleyResponse", "Response is good");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                        Log.i("VolleyError", "Volley Error in receive");
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
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

            } else if (listener != null && ErrorListener == null) {
                request = new StringRequest(Request.Method.DELETE, url, listener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                        Log.i("VolleyError", "Volley Error in receive");
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
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

            } else if (listener == null && ErrorListener != null) {
                request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
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
                Log.d(TAG, "postJSONObjectString: listener != null, ErrorListener != null");
                request = new StringRequest(Request.Method.DELETE, url, listener, ErrorListener) {
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
        } else {
            if (listener == null && ErrorListener == null) {
                request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VolleyResponse", "Response is good");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                        Log.i("VolleyError", "Volley Error in receive");
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
                    }
                });
            } else if (listener != null && ErrorListener == null) {
                request = new StringRequest(Request.Method.DELETE, url, listener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getApplicationContext(), "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                        Log.i("VolleyError", "Volley Error in receive");
                        Log.e(TAG, "Volley: onErrorResponse: ", error);
                    }
                });
            } else if (listener == null && ErrorListener != null) {
                request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VolleyResponse", "Response is good");
                    }
                }, ErrorListener);
            } else {
                Log.d(TAG, "postJSONObjectString: listener != null, ErrorListener != null");
                request = new StringRequest(Request.Method.DELETE, url, listener, ErrorListener);
            }
        }

        request.setShouldCache(true);
        queue.add(request);
    }

}