package com.ept.powersupport.service.qiniu;

import com.ept.powersupport.config.Constants;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 七牛云覆盖上传
 */
@Service
@Slf4j
public class QiniuService {

    //获取授权对象
    Auth auth = Auth.create(Constants.ACCESS_KEY, Constants.SECRET_KEY);


    //自动识别要上传的空间(bucket)的存储区域是华东、华北、华南。
    Zone zone = Zone.autoZone();

    Configuration config = new Configuration(zone);
    UploadManager uploadManager = new UploadManager(config);

    /**
     * 获取凭证
     *
     * @param bucketName
     * @param key
     * @return
     */
    public String getUpToken(String bucketName, String key) {
        //insertOnly 如果希望只能上传指定key的文件，并且不允许修改，那么可以将下面的 insertOnly 属性值设为 1
        return auth.uploadToken(bucketName, key, 3600, new StringMap().put("insertOnly", 0));
    }

    /**
     * 覆盖上传
     *
     * @param file
     * @param bucketName
     * @param key
     */
    public void upload(MultipartFile file, String bucketName, String key) {

        String token = getUpToken(bucketName, key);//获取 token
        try {
            uploadManager.put(file.getBytes(), key, token);//执行上传，通过token来识别 该上传是“覆盖上传”
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除文件
     * @param fileName
     * @return
     */
    public int deleteFile(String fileName){
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.autoZone());
        String key = fileName;
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            Response delete = bucketManager.delete(Constants.BUCKET_NAME, key);
            return delete.statusCode;
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            ex.printStackTrace();
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
        return -1;
    }

}