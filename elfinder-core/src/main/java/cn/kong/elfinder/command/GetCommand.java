/*
 * #%L
 * %%
 * Copyright (C) 2015 Trustsystems Desenvolvimento de Sistemas, LTDA.
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the Trustsystems Desenvolvimento de Sistemas, LTDA. nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package cn.kong.elfinder.command;

import cn.kong.elfinder.ElFinderConstants;
import cn.kong.elfinder.service.ElfinderStorage;
import cn.kong.elfinder.service.VolumeHandler;
import cn.kong.elfinder.util.ImageUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class GetCommand extends AbstractJsonCommand implements ElfinderCommand {

    public static final String ENCODING = "UTF-8";
    public static final String IMAGE_MIME_TYPE = "image";

    @Override
    protected void execute(ElfinderStorage elfinderStorage, HttpServletRequest request, JSONObject json) throws Exception {
        final String target = request.getParameter(ElFinderConstants.ELFINDER_PARAMETER_TARGET);
        final VolumeHandler vh = findTarget(elfinderStorage, target);
        final InputStream is = vh.openInputStream();
        if (vh.getMimeType().contains(IMAGE_MIME_TYPE)) {
            json.put(ElFinderConstants.ELFINDER_PARAMETER_CONTENT, ImageUtil.getImageStr(is, vh.getMimeType()));
        } else {
            final String content = IOUtils.toString(is, ENCODING);
            is.close();
            json.put(ElFinderConstants.ELFINDER_PARAMETER_CONTENT, content);
        }
    }

    public static void main(String[] args) throws IOException {
        String imageStr = ImageUtil.getImageStr(new File("E:/2.png"), "image/jpeg");
        String imageStr1 = ImageUtil.getImageStr(new FileInputStream("E:/2.png"), "image/jpeg");
        System.out.println(imageStr.equals(imageStr1));
    }

}
