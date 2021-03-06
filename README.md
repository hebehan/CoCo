# CoCo
 [![Hex.pm](https://img.shields.io/badge/download-1.0.0-green)](https://www.apache.org/licenses/LICENSE-2.0)
 [![Hex.pm](https://img.shields.io/badge/Api-4.0%2B-yellow)]()
 [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)]()
 [![Hex.pm](https://img.shields.io/badge/Jetpack-AndroidX-red)]()
#### 一款小而美的的Android系统相机拍照和系统相册选择库🐵
 - 一行代码完成从系统相机拍照或者系统相册选择图片
 - 内部适配 7.0 FileProvider文件处理，无需自己额外处理
 - 默认图片处理器自带两种图片压缩策略，并可按需自定义图片处理器
 - 支持Activity、Fragment,图片异步处理自动绑定相关容器生命周期
 - 全新设计的APi-更灵活-更易于理解
 - 完全基于Kotlin编写，与Java兼容
 - 全面适配AndroidX、配置简单，导入方便 
## Installation：

 ![image](https://img-blog.csdnimg.cn/20191009181659912.png)

最新版本(Based on Android X):
```java
dependencies {
    implementation 'com.qw:coco:1.0.0'
}
```
###### 最新Release 改动：
- 全新重构的APi，更易于理解。
- 更解耦，组合操作更灵活

 CoCo 1.0.0  将迎来历史上最大的更新：

 强烈建议您迁移到最新的APi，方便后续新功能的拓展，老版本最后Release 版本将维护至[0.3.1](https://github.com/soulqw/CoCo/blob/developer/README_OLD.md)，后续不再更新(分支master_1.0.0_below)。

## Usage：
#### 基本用法

- 调用系统相机拍照
```kotlin
       CoCo.with(this@MainActivity)
                .take(createSDCardFile())
                .start(object : CoCoCallBack<TakeResult> {

                    override fun onSuccess(data: TakeResult) {
                       iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
                    }

                    override fun onFailed(exception: Exception) {
                    }
                })
```
效果图：

![image](https://cdn.nlark.com/yuque/0/2020/gif/1502571/1601093298091-b091b479-05d0-435e-a650-ba5e07850d72.gif)

- 调用系统相册选择图片：

```kotlin
        CoCo.with(this@MainActivity)
                    .pick()
                    .start(object : CoCoCallBack<PickResult> {
                        override fun onSuccess(data: PickResult) {

                        iv_image.setImageURI(data.originUri)

                        }

                        override fun onFailed(exception: Exception) {
                        }
                    })
```
##### 效果图:

![image](https://cdn.nlark.com/yuque/0/2020/gif/1502571/1601093668141-533ce509-9f4e-45fa-99c7-57a9a3d31335.gif)

- 处理我们拿到的原图：

上述以上是原图的情形，通常情况下，我们常常要对原图做一些处理，比如压缩等，所以CoCo 提供了dispose操作符，方便获得图片之后做一些处理：
```kotlin
        //选择图片后压缩
         CoCo.with(this)
                .pick()
                //切换操作符
                .then()
                .dispose()
                .start(object : CoCoCallBack<DisposeResult> {
                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }
                })

```
我们通过 then 操作符来完成操作符的组合，可以进行一些列操作符的串联流式处理。

##### dispose 操作符：

dispose操作符可以自动在子线程处理我们要处理的文件，并且自动绑定with()容器中的生命周期

###### 它不仅可以和其它操作符组合使用：
```kotlin
 CoCo.with(this)
                .take(createSDCardFile())
                .then()
                .dispose()
                .start(object : CoCoCallBack<DisposeResult> {

                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(Utils.getBitmapFromFile(data.savedFile!!.absolutePath))
                    }

                    override fun onFailed(exception: Exception) {

                    }
                })
```
###### 它还可以单独使用：
```kotlin
        CoCo.with(this)
                .dispose()
                .origin(imageFile.path)
                .start(object : CoCoCallBack<DisposeResult> {

                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }
                })
```
###### 系统默认Default 图片处理器可以帮我们完成图片处理，也可自定义处理逻辑：

```kotlin
              CoCo.with(this)
                .dispose()
                .disposer(CustomDisposer())
              //.disposer(DefaultImageDisposer())
                .origin(imageFile.path)
                .start(object : CoCoCallBack<DisposeResult> {

                    override fun onSuccess(data: DisposeResult) {
                        iv_image.setImageBitmap(data.compressBitmap)
                    }

                    override fun onFailed(exception: Exception) {
                        Log.d(MainActivity.TAG, exception.toString())
                    }
                })

                            /**
             * custom disposer
             * rotation image
             */
            class CustomDisposer : Disposer {
                override fun disposeFile(originPath: String, targetToSaveResult: File?): DisposeResult {
                    return DisposeResult().also {
                        var bitmap = QualityCompressor()
                            .compress(originPath, 80)
                        val m = Matrix()
                        m.postRotate(90f)
                        bitmap = Bitmap.createBitmap(
                            bitmap!!, 0, 0, bitmap.width,
                            bitmap.height, m, true
                        )
                        it.savedFile = targetToSaveResult
                        it.compressBitmap = bitmap
                    }
                }
            }

```
- 其它功能：
###### 每个操作符都可以添加回调监听：

```kotlin
  CoCo.with(this@PickPictureActivity)
                .pick()
                .range(Range.PICK_CONTENT)
//                .range(Range.PICK_DICM)
                .callBack(object : PickCallBack {

                    override fun onFinish(result: PickResult) {
                        Log.d(MainActivity.TAG, "take onFinish${result}")
                    }

                    override fun onCancel() {
                        Log.d(MainActivity.TAG, "take onCancel")
                    }

                    override fun onStart() {
                        Log.d(MainActivity.TAG, "take onStart")
                    }

                }).start(object : CoCoCallBack<PickResult> {

                    override fun onSuccess(data: PickResult) {
                        iv_image.setImageURI(data.originUri)
                    }

                    override fun onFailed(exception: Exception) {}
                })
```
更多功能可参考Demo

### 截图：
![image](https://cdn.nlark.com/yuque/0/2020/png/1502571/1601094243032-2d14deb1-e487-4d6e-906e-fafe6845c654.png)

