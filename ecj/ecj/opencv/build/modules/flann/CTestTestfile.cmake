# CMake generated Testfile for 
# Source directory: /home/derek/Documents/thesis-ecj/ecj/opencv/modules/flann
# Build directory: /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/flann
# 
# This file includes the relevant testing commands required for 
# testing this directory and lists subdirectories to be tested as well.
add_test(opencv_test_flann "/home/derek/Documents/thesis-ecj/ecj/opencv/build/bin/opencv_test_flann" "--gtest_output=xml:opencv_test_flann.xml")
set_tests_properties(opencv_test_flann PROPERTIES  LABELS "Main;opencv_flann;Accuracy" WORKING_DIRECTORY "/home/derek/Documents/thesis-ecj/ecj/opencv/build/test-reports/accuracy" _BACKTRACE_TRIPLES "/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVUtils.cmake;1763;add_test;/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVModule.cmake;1352;ocv_add_test_from_target;/home/derek/Documents/thesis-ecj/ecj/opencv/cmake/OpenCVModule.cmake;1110;ocv_add_accuracy_tests;/home/derek/Documents/thesis-ecj/ecj/opencv/modules/flann/CMakeLists.txt;2;ocv_define_module;/home/derek/Documents/thesis-ecj/ecj/opencv/modules/flann/CMakeLists.txt;0;")
