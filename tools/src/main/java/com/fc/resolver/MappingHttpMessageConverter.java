package com.fc.resolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Controller返回时日期格式是时间戳问题解决
 *
 * @author fangcong on 2018/6/29.
 */
public class MappingHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public final static Charset UTF8 = Charset.forName("UTF-8");

    private Charset charset = UTF8;

    private SerializerFeature[] serializerFeature;

    private String callback = "callback";

    /**
     * 避免返回时间时变成时间戳格式，java.util.Date默认会返回时间戳，java.sql.Date返回日期格式
     */
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
        .disableHtmlEscaping().create();

    public MappingHttpMessageConverter() {
        super(new MediaType("application", "json", UTF8), new MediaType("application", "*+json", UTF8));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public SerializerFeature[] getFeatures() {
        return serializerFeature;
    }

    public void setFeatures(SerializerFeature... features) {
        this.serializerFeature = features;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream in = inputMessage.getBody();
        byte[] buf = new byte[1024];
        for (; ; ) {
            int len = in.read(buf);
            if (len == -1) {
                break;
            }
            if (len > 0) {
                byteArrayOutputStream.write(buf, 0, len);
            }
        }

        byte[] bytes = byteArrayOutputStream.toByteArray();
        if (charset == UTF8) {
            return JSON.parseObject(bytes, clazz);
        } else {
            return JSON.parseObject(bytes, 0, bytes.length, charset.newDecoder(), clazz);
        }
    }

    /**
     * 针对@ResponseBody 标注的方法，返回json或者jsonp的数据格式。是否为jsonp需要从请求中判断是否 callback 的参数
     *
     * @param obj
     * @param outputMessage
     * @throws IOException
     * @throws HttpMessageNotWritableException
     */
    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException {

        String jsonpCallback = null;
        RequestAttributes reqAttrs = RequestContextHolder.currentRequestAttributes();
        if (reqAttrs instanceof ServletRequestAttributes) {
            jsonpCallback = ((ServletRequestAttributes)reqAttrs).getRequest().getParameter(callback);
        }

        String securityStr = GSON.toJson(obj);

        if (StringUtils.isNotBlank(securityStr)) {
            securityStr = securityStr.replaceAll("\t", " ");
        }
        if (jsonpCallback != null) {
            StringBuilder sb = new StringBuilder(jsonpCallback).append("(").append(securityStr).append(")");
            securityStr = sb.toString();
        }

        OutputStream out = outputMessage.getBody();
        byte[] bytes = securityStr.getBytes(charset);

        out.write(bytes);
    }
}
