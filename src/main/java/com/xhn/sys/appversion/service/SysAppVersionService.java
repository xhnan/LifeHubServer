package com.xhn.sys.appversion.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.sys.appversion.model.SysAppVersion;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * 应用版本管理 服务接口
 *
 * @author xhn
 * @date 2026-03-02
 */
public interface SysAppVersionService extends IService<SysAppVersion> {

    /**
     * 发布新版本（上传APK到MinIO）
     *
     * @param versionCode 版本号
     * @param versionName 版本名称
     * @param file        APK文件
     * @param updateLog   更新日志
     * @param isForce     是否强制更新
     * @param platform    平台类型
     * @return 保存的版本信息
     */
    Mono<SysAppVersion> publishVersion(Integer versionCode, String versionName,
                                       FilePart file, String updateLog,
                                       Integer isForce, String platform);

    /**
     * 检查版本更新
     *
     * @param currentVersionCode 当前版本号
     * @param platform           平台类型
     * @return 最新版本信息
     */
    SysAppVersion checkUpdate(Integer currentVersionCode, String platform);

    /**
     * 获取最新版本
     *
     * @param platform 平台类型
     * @return 最新版本信息
     */
    SysAppVersion getLatestVersion(String platform);

    /**
     * 分页查询版本列表
     *
     * @param page     分页参数
     * @param platform 平台类型
     * @return 版本列表
     */
    IPage<SysAppVersion> pageList(Page<SysAppVersion> page, String platform);

    /**
     * 快速上传并发布新版本（自动禁用旧版本）
     *
     * @param versionCode 版本号
     * @param versionName 版本名称
     * @param file        APK文件
     * @param updateLog   更新日志
     * @param isForce     是否强制更新
     * @param platform    平台类型
     * @return 保存的版本信息
     */
    Mono<SysAppVersion> quickPublish(Integer versionCode, String versionName,
                                    FilePart file, String updateLog,
                                    Integer isForce, String platform);
}
