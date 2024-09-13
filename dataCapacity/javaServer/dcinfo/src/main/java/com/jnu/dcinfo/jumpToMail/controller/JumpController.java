package com.jnu.dcinfo.jumpToMail.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jnu.dcinfo.jumpToMail.util.CustomFilePart;
import com.jnu.dcinfo.jumpToMail.util.Result;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;


@CrossOrigin
@RestController
@RequestMapping("/jump/mail")
public class JumpController {

    /**
     * 发送简单邮件
     *
     * @param jsonStr 邮件信息
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/sendMail")
    public Result send(@RequestBody String jsonStr) throws UnsupportedEncodingException {
        String encodeJsonStr = URLEncoder.encode(jsonStr, "UTF-8");

        String targetURL = "http://RemoteServer:Port/mail/sendMail";
        PostMethod postMethod = new PostMethod(targetURL);
        try {
            Part[] parts = {
                    new StringPart("jsonStr", encodeJsonStr),
            };
            postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(postMethod);

            if (status == HttpStatus.SC_OK) {
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str= "";
                while((str = br.readLine()) != null){
                    stringBuffer.append(str);
                }
                JSONObject responseJson = JSON.parseObject(stringBuffer.toString());
                return new Result(responseJson.getObject("success", Boolean.class), responseJson.getObject("msg", String.class), responseJson.getObject("data", Object.class));
            } else {
                return new Result(true, "邮件发送失败，请联系工作人员", null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(true, "邮件发送失败，请联系工作人员", null);
        } finally {
            postMethod.releaseConnection();
        }
    }

    /**
     * 发送含复杂邮件
     *
     * @param attachment 附件
     * @param jsonStr    邮件信息
     * @return
     * @throws IOException
     */
    @RequestMapping("/sendMailComplex")
    public Result sendMailComplex(MultipartFile attachment,
                                  @RequestParam(value = "subject", defaultValue = "") String subject,
                                  @RequestParam(value = "body",defaultValue = "") String body,
                                  @RequestParam(value = "receiverList", defaultValue = "") String receiverList) throws IOException {

        String subjectEncode = URLEncoder.encode(subject, "UTF-8");
        String bodyEncode = URLEncoder.encode(body, "UTF-8");
        String receiverListEncode = URLEncoder.encode(receiverList, "UTF-8");

        String targetURL = "http://RemoteServer:Port/mail/sendMailComplex";
        PostMethod postMethod = new UTF8PostMethod(targetURL);

        Part[] parts = new Part[4];
        parts[0] = new StringPart("subject", subjectEncode);
        parts[1] = new StringPart("body", bodyEncode);
        parts[2] = new StringPart("receiverList", receiverListEncode);
        File file = null;

        if (attachment != null && !attachment.isEmpty()){
            try {
                String originalFilename = attachment.getOriginalFilename();
                int pointIndex = originalFilename.lastIndexOf(".");
                String tDir = System.getProperty("java.io.tmpdir");
                file = new File(tDir, originalFilename);
                attachment.transferTo(file);
            } catch (IOException e) {
                e.printStackTrace();
                return new Result(true, "文件上传失败", null);
            }
        }

        try {
            parts[3] = new CustomFilePart("attachment", file);
            postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(postMethod);
            if (file != null) {
                file.delete();
            }
            if (status == HttpStatus.SC_OK) {
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str= "";
                while((str = br.readLine()) != null){
                    stringBuffer.append(str);
                }
                JSONObject responseJson = JSON.parseObject(stringBuffer.toString());
                return new Result(responseJson.getObject("success", Boolean.class), responseJson.getObject("msg", String.class), responseJson.getObject("data", Object.class));
            } else {
                return new Result(true, "邮件发送失败，请联系工作人员", null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(true, "邮件发送失败，请联系工作人员", null);
        } finally {
            postMethod.releaseConnection();
        }
    }

    /**
     * 发送钉钉通知
     *
     * @param jsonStr 钉钉信息
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/sendByDingTalk")
    public Result sendByDingTalk(@RequestBody String jsonStr) throws UnsupportedEncodingException {
        String encodeJsonStr = URLEncoder.encode(jsonStr, "UTF-8");

        String targetURL = "http://RemoteServer:Port/mail/sendByDingTalk";
        PostMethod postMethod = new PostMethod(targetURL);
        try {
            Part[] parts = {
                    new StringPart("jsonStr", encodeJsonStr),
            };
            postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str= "";
                while((str = br.readLine()) != null){
                    stringBuffer.append(str);
                }
                JSONObject responseJson = JSON.parseObject(stringBuffer.toString());
                return new Result(responseJson.getObject("success", Boolean.class), responseJson.getObject("msg", String.class), responseJson.getObject("data", Object.class));
            } else {
                return new Result(true, "消息发送失败，请联系工作人员", null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(true, "消息发送失败，请联系工作人员", null);
        } finally {
            postMethod.releaseConnection();
        }
    }

    /**
     * 钉钉机器人与邮箱发送消息
     *
     * @param attachment 邮件附件
     * @param jsonStr    详细信息
     * @return
     */
    @RequestMapping("/sendByEmailAndDingTalk")
    public Result sendByEmailAndDingTalk(MultipartFile attachment,
                                         @RequestParam(value = "subject", defaultValue = "") String subject,
                                         @RequestParam(value = "body",defaultValue = "") String body,
                                         @RequestParam(value = "receiverList", defaultValue = "") String receiverList,
                                         @RequestParam("jsonStr") String jsonStr) throws UnsupportedEncodingException {

        String subjectEncode = URLEncoder.encode(subject, "UTF-8");
        String bodyEncode = URLEncoder.encode(body, "UTF-8");
        String receiverListEncode = URLEncoder.encode(receiverList, "UTF-8");
        String encodeJsonStr = URLEncoder.encode(jsonStr, "UTF-8");

        String targetURL = "http://RemoteServer:Port/mail/sendByEmailAndDingTalk";
        PostMethod postMethod = new PostMethod(targetURL);
        Part[] parts = new Part[5];
        parts[0] = new StringPart("subject", subjectEncode);
        parts[1] = new StringPart("body", bodyEncode);
        parts[2] = new StringPart("receiverList", receiverListEncode);
        parts[3] = new StringPart("jsonStr", encodeJsonStr);
        File file = null;

        if (attachment != null && !attachment.isEmpty()){
            try {
                String originalFilename = attachment.getOriginalFilename();
                int pointIndex = originalFilename.lastIndexOf(".");
                String fileSuffix = originalFilename.substring(pointIndex);//截取文件后缀
                String preSuffix = originalFilename.substring(0, pointIndex);
                file = File.createTempFile(preSuffix, fileSuffix);
                attachment.transferTo(file);
                file.deleteOnExit();
            } catch (IOException e) {
                e.printStackTrace();
                return new Result(true, "文件上传失败", null);
            }
        }

        try {
            parts[4] = new FilePart("attachment", file);

            postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str= "";
                while((str = br.readLine()) != null){
                    stringBuffer.append(str);
                }
                JSONObject responseJson = JSON.parseObject(stringBuffer.toString());
                return new Result(responseJson.getObject("success", Boolean.class), responseJson.getObject("msg", String.class), responseJson.getObject("data", Object.class));
            } else {
                return new Result(true, "发送失败，请联系工作人员", null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(true, "发送失败，请联系工作人员", null);
        } finally {
            postMethod.releaseConnection();
        }
    }

    public static class UTF8PostMethod extends PostMethod{
        public UTF8PostMethod(String url){
            super(url);
        }
        @Override
        public String getRequestCharSet() {
            //return super.getRequestCharSet();
            return "UTF-8";
        }
    }

}