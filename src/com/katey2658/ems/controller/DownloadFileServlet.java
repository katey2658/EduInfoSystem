package com.katey2658.ems.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * Created by 11456 on 2016/11/23.
 */
public class DownloadFileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //得到要下载的文件名
        String fileName=request.getParameter("fileName");
        fileName=new String(fileName.getBytes("ISO-8859-1"),"UTF-8");
        //注：上传的文件都是保存在/WEB-INF/upload目录下面的子目录
        String fileSaveRootPath=this.getServletContext().getRealPath("/WEB-INF/upload");
        //通过文件名找出文件所在的目录
        String filePath=getFileSavePathByFileName(fileSaveRootPath,fileName);
        //下载的文件
        File file=new File(filePath+"\\"+fileName);
        //文件不存在
        if (!file.exists()){
            request.setAttribute("message","您要下载的资源已经被删除");
            request.getRequestDispatcher("/message.jsp").forward(request,response);
        }
        //处理文件名
        String realName=fileName.substring(fileName.indexOf("_")+1);
        response.setHeader("content-disposition","attachmen;filename="+ URLEncoder.encode(realName,"UTF-8"));
        //读取要下载的文件，保存到文件输出流
        FileInputStream in=new FileInputStream(filePath+"\\"+fileName);
        //创建输出流
        OutputStream out=response.getOutputStream();
        //创建缓冲区
        byte[] buffer=new byte[1024];
        int len=0;
        while((len=in.read(buffer))>0){
            //输出缓冲区内容到浏览器，实现文件下载
            out.write(buffer,0,len);
        }
        //关闭输入流
        in.close();
        //关闭输出流
        out.close();

    }

    /**
     *
     * @param fileSaveRootPath 文件存储根目录
     * @param fileName 文件名
     * @return 文件路径
     */
    private String getFileSavePathByFileName(String fileSaveRootPath, String fileName) {
        int hashcode=fileName.hashCode();
        int dir1=hashcode&0xf;
        int dir2=(hashcode&0xf0)>>4;
        String dir=fileSaveRootPath+"\\"+dir1+"\\"+dir2;
        File file=new File(dir);
        if (!file.exists()){
            file.mkdirs();
        }
        return dir;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
