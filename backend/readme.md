# 打包成 jarc
- docker build -t mark129428xxx/gemini-admin:latest .

- docker run -p 8080:8080 mark129428xxx/gemini-admin:latest

- docker push mark129428xxx/gemini-admin:latest

- docker login --username=mark129428 crpi-p9vd9z0fqoe0ox41.cn-hangzhou.personal.cr.aliyuncs.com
  - docker tag 4a5aee5ad086 crpi-p9vd9z0fqoe0ox41.cn-hangzhou.personal.cr.aliyuncs.com/hhk17870558970/docker:latest2
    - docker push crpi-p9vd9z0fqoe0ox41.cn-hangzhou.personal.cr.aliyuncs.com/hhk17870558970/docker:latest2
      -docker pull crpi-p9vd9z0fqoe0ox41.cn-hangzhou.personal.cr.aliyuncs.com/hhk17870558970/docker:latest2
      -:latest
```bash
docker run -d \
-p 8080:8080 \
--name gemini-backend \
gemini-backend 
```

```mermaid

```