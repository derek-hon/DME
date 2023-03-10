//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.xfeatures2d;

import org.opencv.features2d.Feature2D;
import org.opencv.xfeatures2d.LUCID;

// C++: class LUCID
/**
 * Class implementing the locally uniform comparison image descriptor, described in CITE: LUCID
 *
 * An image descriptor that can be computed very fast, while being
 * about as robust as, for example, SURF or BRIEF.
 *
 * <b>Note:</b> It requires a color image as input.
 */
public class LUCID extends Feature2D {

    protected LUCID(long addr) { super(addr); }

    // internal usage only
    public static LUCID __fromPtr__(long addr) { return new LUCID(addr); }

    //
    // C++: static Ptr_LUCID cv::xfeatures2d::LUCID::create(int lucid_kernel = 1, int blur_kernel = 2)
    //

    /**
     * @param lucid_kernel kernel for descriptor construction, where 1=3x3, 2=5x5, 3=7x7 and so forth
     * @param blur_kernel kernel for blurring image prior to descriptor construction, where 1=3x3, 2=5x5, 3=7x7 and so forth
     * @return automatically generated
     */
    public static LUCID create(int lucid_kernel, int blur_kernel) {
        return LUCID.__fromPtr__(create_0(lucid_kernel, blur_kernel));
    }

    /**
     * @param lucid_kernel kernel for descriptor construction, where 1=3x3, 2=5x5, 3=7x7 and so forth
     * @return automatically generated
     */
    public static LUCID create(int lucid_kernel) {
        return LUCID.__fromPtr__(create_1(lucid_kernel));
    }

    /**
     * @return automatically generated
     */
    public static LUCID create() {
        return LUCID.__fromPtr__(create_2());
    }


    //
    // C++:  void cv::xfeatures2d::LUCID::setLucidKernel(int lucid_kernel)
    //

    public void setLucidKernel(int lucid_kernel) {
        setLucidKernel_0(nativeObj, lucid_kernel);
    }


    //
    // C++:  int cv::xfeatures2d::LUCID::getLucidKernel()
    //

    public int getLucidKernel() {
        return getLucidKernel_0(nativeObj);
    }


    //
    // C++:  void cv::xfeatures2d::LUCID::setBlurKernel(int blur_kernel)
    //

    public void setBlurKernel(int blur_kernel) {
        setBlurKernel_0(nativeObj, blur_kernel);
    }


    //
    // C++:  int cv::xfeatures2d::LUCID::getBlurKernel()
    //

    public int getBlurKernel() {
        return getBlurKernel_0(nativeObj);
    }


    //
    // C++:  String cv::xfeatures2d::LUCID::getDefaultName()
    //

    public String getDefaultName() {
        return getDefaultName_0(nativeObj);
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++: static Ptr_LUCID cv::xfeatures2d::LUCID::create(int lucid_kernel = 1, int blur_kernel = 2)
    private static native long create_0(int lucid_kernel, int blur_kernel);
    private static native long create_1(int lucid_kernel);
    private static native long create_2();

    // C++:  void cv::xfeatures2d::LUCID::setLucidKernel(int lucid_kernel)
    private static native void setLucidKernel_0(long nativeObj, int lucid_kernel);

    // C++:  int cv::xfeatures2d::LUCID::getLucidKernel()
    private static native int getLucidKernel_0(long nativeObj);

    // C++:  void cv::xfeatures2d::LUCID::setBlurKernel(int blur_kernel)
    private static native void setBlurKernel_0(long nativeObj, int blur_kernel);

    // C++:  int cv::xfeatures2d::LUCID::getBlurKernel()
    private static native int getBlurKernel_0(long nativeObj);

    // C++:  String cv::xfeatures2d::LUCID::getDefaultName()
    private static native String getDefaultName_0(long nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
