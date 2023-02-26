# CMake generated Testfile for 
# Source directory: /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/ximgproc
# Build directory: /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/ximgproc
# 
# This file includes the relevant testing commands required for 
# testing this directory and lists subdirectories to be tested as well.
add_test(opencv_test_ximgproc "/home/derek/Documents/thesis-ecj/ecj/opencv/build/bin/opencv_test_ximgproc" "--gtest_output=xml:opencv_test_ximgproc.xml")
set_tests_properties(opencv_test_ximgproc PROPERTIES  LABELS "Extra;opencv_ximgproc;Accuracy" WORKING_DIRECTORY "/home/derek/Documents/thesis-ecj/ecj/opencv/build/test-reports/accuracy" _BACKTRACE_TRIPLES "/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVUtils.cmake;1763;add_test;/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVModule.cmake;1352;ocv_add_test_from_target;/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVModule.cmake;1110;ocv_add_accuracy_tests;/home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/ximgproc/CMakeLists.txt;2;ocv_define_module;/home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/ximgproc/CMakeLists.txt;0;")
add_test(opencv_perf_ximgproc "/home/derek/Documents/thesis-ecj/ecj/opencv/build/bin/opencv_perf_ximgproc" "--gtest_output=xml:opencv_perf_ximgproc.xml")
set_tests_properties(opencv_perf_ximgproc PROPERTIES  LABELS "Extra;opencv_ximgproc;Performance" WORKING_DIRECTORY "/home/derek/Documents/thesis-ecj/ecj/opencv/build/test-reports/performance" _BACKTRACE_TRIPLES "/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVUtils.cmake;1763;add_test;/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVModule.cmake;1251;ocv_add_test_from_target;/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVModule.cmake;1111;ocv_add_perf_tests;/home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/ximgproc/CMakeLists.txt;2;ocv_define_module;/home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/ximgproc/CMakeLists.txt;0;")
add_test(opencv_sanity_ximgproc "/home/derek/Documents/thesis-ecj/ecj/opencv/build/bin/opencv_perf_ximgproc" "--gtest_output=xml:opencv_perf_ximgproc.xml" "--perf_min_samples=1" "--perf_force_samples=1" "--perf_verify_sanity")
set_tests_properties(opencv_sanity_ximgproc PROPERTIES  LABELS "Extra;opencv_ximgproc;Sanity" WORKING_DIRECTORY "/home/derek/Documents/thesis-ecj/ecj/opencv/build/test-reports/sanity" _BACKTRACE_TRIPLES "/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVUtils.cmake;1763;add_test;/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVModule.cmake;1252;ocv_add_test_from_target;/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVModule.cmake;1111;ocv_add_perf_tests;/home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/ximgproc/CMakeLists.txt;2;ocv_define_module;/home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/ximgproc/CMakeLists.txt;0;")
