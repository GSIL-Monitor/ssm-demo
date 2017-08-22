package com.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.fc.bean.FieldVO;

/*import com.alibaba.com.caucho.hessian.io.HessianSerializerInput;
import com.alibaba.com.caucho.hessian.io.HessianSerializerOutput;*/

/**
 * @author fangcong on 2017/8/21.
 */
public class SerializeDemo {

    /**
     * 纯hessian序列化
     * @param object  对象
     * @return byte[] 字节流
     */
    public static byte[] hessianSerialize(Object object) throws Exception {
        if (object == null) {
            throw new NullPointerException();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianSerializerOutput hso = new HessianSerializerOutput(bos);
        hso.writeObject(object);
        return bos.toByteArray();
    }

    /**
     * 纯hessian反序列化
     * @param bytes  字节流
     * @return Object 对象
     * @throws Exception
     */
    public static Object hessianDeserialize(byte[] bytes) throws Exception {
        if (bytes == null) {
            throw new NullPointerException();
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        HessianSerializerInput hsi = new HessianSerializerInput(bis);
        Object object = hsi.readObject();
        return object;
    }

    /**
     * java序列化
     * @param object
     * @return
     */
    public static byte[] javaSerialize(Object object) throws Exception {
        if (object == null) {
            throw new NullPointerException();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        return bos.toByteArray();
    }

    /**
     * java反序列化
     * @param bytes
     * @return
     * @throws Exception
     */
    public static Object javaDeserialize(byte[] bytes) throws Exception {
        if (bytes == null) {
            throw new NullPointerException();
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object object = ois.readObject();
        return object;
    }

    public static void main(String[] args) throws Exception{
        FieldVO fieldVO = new FieldVO();
        long startTime = System.nanoTime();
        byte[] bytes1 = hessianSerialize(fieldVO);
        System.out.println(bytes1);
        FieldVO object1 = (FieldVO) hessianDeserialize(bytes1);
        System.out.println(object1.toString());
        long costTime = System.nanoTime() - startTime;
        System.out.println("hessian cost : " + costTime);
        long startTime1 = System.nanoTime();
        byte[] bytes2 = javaSerialize(fieldVO);
        System.out.println(bytes2);
        FieldVO object2 = (FieldVO)javaDeserialize(bytes2);
        System.out.println(object2.toString());
        long costTime1 = System.nanoTime() - startTime1;
        System.out.println("java cost : " + costTime1);
    }
}
