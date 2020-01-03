package cn.kong.web.controller;

import cn.kong.elfinder.ElFinderConstants;
import cn.kong.elfinder.command.ElfinderCommand;
import cn.kong.elfinder.command.ElfinderCommandFactory;
import cn.kong.elfinder.core.ElfinderContext;
import cn.kong.elfinder.service.ElfinderStorageFactory;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("elfinder/connector")
public class ElfinderController {

    private static final Logger logger = LoggerFactory.getLogger(ElfinderController.class);

    public static final String OPEN_STREAM = "openStream";
    public static final String GET_PARAMETER = "getParameter";

    @Resource(name = "commandFactory")
    private ElfinderCommandFactory elfinderCommandFactory;

    @Resource(name = "elfinderStorageFactory")
    private ElfinderStorageFactory elfinderStorageFactory;

    @RequestMapping
    public void connector(HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            request = processMultipartContent(request);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }


        String cmd = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_COMMAND);
        ElfinderCommand elfinderCommand = elfinderCommandFactory.get(cmd);

        try {
            final HttpServletRequest protectedRequest = request;
            elfinderCommand.execute(new ElfinderContext() {
                @Override
                public ElfinderStorageFactory getVolumeSourceFactory() {
                    return elfinderStorageFactory;
                }

                @Override
                public HttpServletRequest getRequest() {
                    return protectedRequest;
                }

                @Override
                public HttpServletResponse getResponse() {
                    return response;
                }
            });
        } catch (Exception e) {
            logger.error("Unknown error", e);
        }
    }

    private HttpServletRequest processMultipartContent(final HttpServletRequest request) throws Exception {
        if (!ServletFileUpload.isMultipartContent(request))
            return request;

        Map<String,String[]> map = request.getParameterMap();

        final Map<String, Object> requestParams = new HashMap<>();

        for(String key : map.keySet()){
            String[] obj = map.get(key);
            if(obj.length == 1){
                requestParams.put(key,obj[0]);
            }else{
                requestParams.put(key,obj);
            }

        }

        AbstractMultipartHttpServletRequest multipartHttpServletRequest = (AbstractMultipartHttpServletRequest)request;

        ServletFileUpload servletFileUpload = new ServletFileUpload();
        String characterEncoding = request.getCharacterEncoding();
        if (characterEncoding == null) {
            characterEncoding = "UTF-8";
        }
        servletFileUpload.setHeaderEncoding(characterEncoding);

        List<MultipartFile> fileList = multipartHttpServletRequest.getFiles("upload[]");

        List<FileItemStream> listFiles = new ArrayList<>();

        for(MultipartFile file : fileList){

            FileItemStream item = createFileItemStream(file);
            InputStream stream = item.openStream();
            String fileName = item.getName();
            if (fileName != null && !fileName.trim().isEmpty()) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                IOUtils.copy(stream, os);
                final byte[] bs = os.toByteArray();
                stream.close();

                listFiles.add((FileItemStream) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                        new Class[]{FileItemStream.class}, new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                if (OPEN_STREAM.equals(method.getName())) {
                                    return new ByteArrayInputStream(bs);
                                }

                                return method.invoke(item, args);
                            }
                        }));
            }

        }

        request.setAttribute(FileItemStream.class.getName(), listFiles);
        return (HttpServletRequest) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{HttpServletRequest.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
                        if (GET_PARAMETER.equals(arg1.getName())) {
                            return requestParams.get(arg2[0]);
                        }

                        return arg1.invoke(request, arg2);
                    }
                });
    }


    private FileItemStream createFileItemStream(MultipartFile file){
        return new FileItemStream() {
            @Override
            public InputStream openStream() throws IOException {
                return file.getInputStream();
            }

            @Override
            public String getContentType() {
                return file.getContentType();
            }

            @Override
            public String getName() {
                return file.getOriginalFilename();
            }

            @Override
            public String getFieldName() {
                return file.getName();
            }

            @Override
            public boolean isFormField() {
                return false;
            }

            @Override
            public FileItemHeaders getHeaders() {
                return null;
            }

            @Override
            public void setHeaders(FileItemHeaders fileItemHeaders) {

            }
        };
    }
}
