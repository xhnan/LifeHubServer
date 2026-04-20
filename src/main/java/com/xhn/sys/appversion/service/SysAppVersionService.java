package com.xhn.sys.appversion.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.sys.appversion.model.SysAppVersion;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface SysAppVersionService extends IService<SysAppVersion> {

    Mono<SysAppVersion> publishVersion(Integer versionCode, String versionName,
                                       FilePart file, String updateLog,
                                       Integer isForce, String platform);

    SysAppVersion checkUpdate(Integer currentVersionCode, String platform);

    SysAppVersion getLatestVersion(String platform);

    IPage<SysAppVersion> pageList(Page<SysAppVersion> page, String platform);

    Mono<SysAppVersion> quickPublish(Integer versionCode, String versionName,
                                     FilePart file, String updateLog,
                                     Integer isForce, String platform,
                                     Long publishedByUserId, Long publishedByApiKeyId,
                                     String publishSource);
}
