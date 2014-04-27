=======
PhotoShoter
===========

Application for sending photo to users on map. Server for this application: https://github.com/yoman07/geo-server 

Contributors
-------

- [Bartłomiej Gołuszka aka `bbaloo`](https://github.com/bbaloo)
- [Roman Barzyczak aka `yoman07`](https://github.com/yoman07)

Events
-------

####Getting images

For getting images you need subscribe to Bus with method: `public  void onEvent(ImageEvent imageEvent)` , `imageEvent` contain method `getLocation()` - positon where image was made, `getBase64image()` - base64 image, you can convert it by using `ImageHelper.bitmapFromBase64Format(base64String)`, `getSenderId` - method for get user id, who made a photo.

Example:
```
public  void onEvent(ImageEvent imageEvent) {
        Log.i(TAG, "Got imageEvent with data" + imageEvent.toString());
        Bitmap bitmap = ImageHelper.bitmapFromBase64Format(imageEvent.getBase64image());
        Log.i(TAG, "Bitmap " + bitmap.toString());
    }
```

####Sending images

You can sending image to specific user by post event `MyImageEvent` which containt `base64image` - image in base64 format ( you can convert bitmap to base64 by using `ImageHelper.base64FormatFromBitmap(bm)` ), `receiverId` - user who should receive image

```
    private void sendImageEvent(Bitmap bm, Location location, String receiverId) {
            MyImageEvent imageEvent = new MyImageEvent(ImageHelper.base64FormatFromBitmap(bm),location, receiverId);
            EventBus.getDefault().post(imageEvent);
    }
```


License
-------

Copyright (c) 2014 Roman Barzyczak & Bartłomiej Gołuszka

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
