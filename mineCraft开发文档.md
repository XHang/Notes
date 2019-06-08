# 资源包开发文档

1. 首先建立一个文件夹，作为这个资源包的存放路径

2. 在这个文件夹里面，创建一个`manifest.json`文件

   里面的内容大致如此

   ```
   {
     "format_version": 1,
  "header": {
       "description": "这是一个教程资源包",
       "name": "教程资源包",
       "uuid": "bc2a0bc9-e3a2-4702-9319-d4d3231dfdbc",
       "version": [0, 0, 1]
     },
     "modules": [
       {
         "description": "教程资源包",
         "type": "resources",
         "uuid": "891f5751-bb0e-47c6-91f0-fdc4e76949ef",
         "version": [0, 0, 1]
       }
     ]
   }
   ```
   
   首先，里面的UUID必须都是不一样的，自己生成去。
   
3. 再创建一个png，命名为`pack_icon.png `确保图片必须是128*128像素，该图片作为资源包的图片将显示在游戏上

4. 

