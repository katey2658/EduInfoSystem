package com.katey2658.ems.controller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by 11456 on 2016/11/23.
 */
public class UploadFileHanlderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       //得到上传文件的保存目录，将上传的文件存放在 WEB-INF目录下，不允许外界访问，保证上传文件的安全
        String saveFilePath=this.getServletContext().getRealPath("/WEB-INF/upload/image");
        //上传文件的临时保存目录
        String tempPath=this.getServletContext().getRealPath("/WEB-INF/temp");
        File tempFile=new File(tempPath);
        //判断上传文件的保存目录是否存在
        if(!tempFile.exists()&&!tempFile.isDirectory()){
            //目录不存在，创建目录
            tempFile.mkdir();
        }
        String message="";
        try{
            //使用apache文件上传组件处理上传文件
            //步骤1.创建一个DiskFileItemFactory工厂
            DiskFileItemFactory factory=new DiskFileItemFactory();

            //设置缓冲区的大小，当上传文件超过缓冲区的大小，就会生成一个临时文件放到指定临时目录中
            factory.setSizeThreshold(1024*100);//缓冲区大小是100kb,如果不指定，那么缓冲区大小默认是10ko

            //设置上传时候生成的临时文件保存目录
            factory.setRepository(tempFile);

            //步骤2.创建一个文件上传解析器
            ServletFileUpload fileUpload=new ServletFileUpload(factory);

            //监听文件上传的进度
            fileUpload.setProgressListener(new ProgressListener() {
                @Override
                public void update(long l, long l1, int i) {
                    System.out.println("文件大小："+l1+",当前已经处理："+l);
                    /*
                    这里应该和页面进行交互，然后在页面上可以查看进度
                     */
                }
            });

            //解决中文乱码
            fileUpload.setHeaderEncoding("UTF-8");
            //步骤3.判断上传的是否是表单的数据
            if (!ServletFileUpload.isMultipartContent(request)){
                //对表单进行数据获取
                return;
            }
            //设置单个文件的大小的最大值，设置为2M
            fileUpload.setFileSizeMax(1024*1024*2);
            //设置上传文件总量的最大值，最大值=同时传的多个文件的最大值的总和，目前设置为10M
            fileUpload.setSizeMax(1024*1024*10);
            //步骤4.使用解析器解析上传的数据，解析结果放在一个List<FileItem>集合，每个FileItem对应一个表单的输入项
            List<FileItem> list=fileUpload.parseRequest(request);
            //对每一个数据项进行检查
            for(FileItem fileItem:list){
                if (fileItem.isFormField()){
                    String fileItemName=fileItem.getFieldName();
                    //解决普通输入项的数据的中文乱码问题
                    String value=fileItem.getString("UTF-8");
                        /*
                        可以做一些普通数据表单数据的处理
                         */
                }else{
                    //数据项中存放的是一个文件
                    //文件名
                    String fileName=fileItem.getName();
                    //文件名不存在或者文件名名字有误
                    if (fileName==null||fileName.trim().isEmpty()){
                        continue;
                    }
                    //注：不同浏览器提交的文件名不一样的，有些浏览器提交上来的文件名是带有路径的，有些只是单纯的名字

                    //获取到带有文件路径文件，只保留文件名
                    fileName=fileName.substring(fileName.lastIndexOf("\\")+1);

                    //得到上传文件的拓展名,根据拓展名做一些判断的
                    String fileExtName=fileName.substring(fileName.lastIndexOf("."+1));

                    //获取上传文件的输入流
                    InputStream in=fileItem.getInputStream();
                    //得到文件保存的名称
                    String saveFilename=makeFileName(fileName);
                    //得到文件的保存目录
                    String realSavePath=makePath(saveFilePath,saveFilename );
                    //创建一个文件输出流
                    FileOutputStream outputStream=new FileOutputStream(saveFilePath+"\\"+fileName);
                    //创建一个缓冲区
                    byte[] buffer=new byte[1024];
                    int len=0;
                    while ((len=in.read(buffer))>0){
                        outputStream.write(buffer,0,len);
                    }
                    //关闭输入流
                    in.close();
                    //关闭输出流
                    outputStream.close();

                    //删除处理文件上传时候生成的临时文件
                    fileItem.delete();
                    message="文件上传成功";
                }


            }
        } catch (FileUploadBase.FileSizeLimitExceededException e){
            message="单个文件超出最大值";
            e.printStackTrace();

        } catch (FileUploadBase.SizeLimitExceededException e){
            message="上传文件总大小超出限制";
            e.printStackTrace();

        } catch (FileUploadException e) {
            message="上传文件失败！";
            e.printStackTrace();
        } finally {
            request.setAttribute("message",message);
            request.getRequestDispatcher("/message.jsp").forward(request,response);
        }
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


    /**
     * 根据一个文件名得到一个文件存储路径
     * @param saveFilePath  文件存储路径
     * @param saveFilename 文件名，根据文件名生成存储目录
     * @return 新的存储目录
     */
    private String makePath(String saveFilePath, String saveFilename) {

        //得到文件的hashcode值
        int hashcode=saveFilename.hashCode();
        int dir1=hashcode&0xf;
        int dir2=(hashcode&0xf0)>>4;

        String dir=saveFilePath+"\\"+dir1+"\\"+dir2;
        //File既可以代表问价也可以代表目录
        File file=new File(dir);
        //如果目录不存在
        if (!file.exists()){
            file.mkdirs();
        }
        return dir;
    }


    /**
     * 得到一个新的文件名
     * @param fileName 原来的文件名
     * @return 新文件名
     */
    private String makeFileName(String fileName){
        return UUID.randomUUID().toString()+"_"+fileName;
    }

}
