# OnlyOffice Personal WASM 资源

此目录用于存放 OnlyOffice Personal 9.3 版本的 WASM 静态资源。

## 获取方式

从 [fernfei/OnlyofficePersonal](https://github.com/fernfei/OnlyofficePersonal) 仓库下载 9.3 版本：

```bash
git clone -b 9.3.0.133 https://github.com/fernfei/OnlyofficePersonal.git
cp -r OnlyofficePersonal/* ./onlyoffice/
```

## 注意事项

- WASM 文件较大（约 40MB），建议使用预压缩（gzip/brotli）+ 强缓存
- 不建议将 WASM 文件提交到 Git，通过 `.gitignore` 排除
- Docker 部署时通过 COPY 指令复制到容器中
