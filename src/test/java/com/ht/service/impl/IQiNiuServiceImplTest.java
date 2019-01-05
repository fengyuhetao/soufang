package com.ht.service.impl;

import com.ht.SoufangApplicationTests;
import com.ht.base.AppErrorController;
import com.ht.service.IQiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import static org.junit.Assert.*;

public class IQiNiuServiceImplTest extends SoufangApplicationTests{
    @Autowired
    private IQiNiuService qiNiuService;

    @Test
    public void uploadFile() throws Exception {
        String fileName = "D:\\java\\soufang\\src\\test\\java\\com\\ht\\service\\impl\\1.jpg";
        File file = new File(fileName);

        Assert.assertTrue(file.exists());
        try {
            Response response = this.qiNiuService.uploadFile(file);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delete() {
        String key = "FsNkzDULwPRnjutWVgaOzlKsHW-q";
        Response response = null;
        try {
            response = qiNiuService.delete(key);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }
}