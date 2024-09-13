package com.jnu.dcinfo.jumpToMail.util;

import com.alibaba.druid.util.StringUtils;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.util.EncodingUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author duya001
 * 
 */
public class CustomFilePart extends FilePart {

    public CustomFilePart(String name, File file) throws FileNotFoundException {
        super(name, file);
    }

    @Override
    protected void sendDispositionHeader(OutputStream out) throws IOException {
        //super.sendDispositionHeader(out);
        //下面部分其实为实现父类super.sendDispositionHeader(out) 里面的逻辑
        out.write(CONTENT_DISPOSITION_BYTES);
        out.write(QUOTE_BYTES);
        out.write(EncodingUtil.getBytes(getName(),"utf-8"));
        out.write(QUOTE_BYTES);
        String fileName = getSource().getFileName();
        // 此处为解决中文文件名乱码部分
        if (!StringUtils.isEmpty(fileName)){
            out.write(EncodingUtil.getAsciiBytes(FILE_NAME));
            out.write(QUOTE_BYTES);
            out.write(EncodingUtil.getBytes(fileName,"utf-8"));
            out.write(QUOTE_BYTES);
        }
    }
}
