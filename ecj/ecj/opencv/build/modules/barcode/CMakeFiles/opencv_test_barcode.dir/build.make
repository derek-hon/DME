# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.22

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/derek/Documents/thesis-ecj/ecj/opencv

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/derek/Documents/thesis-ecj/ecj/opencv/build

# Include any dependencies generated for this target.
include modules/barcode/CMakeFiles/opencv_test_barcode.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include modules/barcode/CMakeFiles/opencv_test_barcode.dir/compiler_depend.make

# Include the progress variables for this target.
include modules/barcode/CMakeFiles/opencv_test_barcode.dir/progress.make

# Include the compile flags for this target's objects.
include modules/barcode/CMakeFiles/opencv_test_barcode.dir/flags.make

modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.o: modules/barcode/CMakeFiles/opencv_test_barcode.dir/flags.make
modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.o: /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/barcode/test/test_barcode.cpp
modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.o: modules/barcode/CMakeFiles/opencv_test_barcode.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/derek/Documents/thesis-ecj/ecj/opencv/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.o"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/barcode && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.o -MF CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.o.d -o CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.o -c /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/barcode/test/test_barcode.cpp

modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.i"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/barcode && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/barcode/test/test_barcode.cpp > CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.i

modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.s"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/barcode && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/barcode/test/test_barcode.cpp -o CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.s

modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.o: modules/barcode/CMakeFiles/opencv_test_barcode.dir/flags.make
modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.o: /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/barcode/test/test_main.cpp
modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.o: modules/barcode/CMakeFiles/opencv_test_barcode.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/derek/Documents/thesis-ecj/ecj/opencv/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.o"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/barcode && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.o -MF CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.o.d -o CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.o -c /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/barcode/test/test_main.cpp

modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.i"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/barcode && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/barcode/test/test_main.cpp > CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.i

modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.s"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/barcode && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/barcode/test/test_main.cpp -o CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.s

# Object files for target opencv_test_barcode
opencv_test_barcode_OBJECTS = \
"CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.o" \
"CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.o"

# External object files for target opencv_test_barcode
opencv_test_barcode_EXTERNAL_OBJECTS =

bin/opencv_test_barcode: modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_barcode.cpp.o
bin/opencv_test_barcode: modules/barcode/CMakeFiles/opencv_test_barcode.dir/test/test_main.cpp.o
bin/opencv_test_barcode: modules/barcode/CMakeFiles/opencv_test_barcode.dir/build.make
bin/opencv_test_barcode: lib/libopencv_ts.a
bin/opencv_test_barcode: lib/libopencv_barcode.so.4.7.0
bin/opencv_test_barcode: lib/libopencv_highgui.so.4.7.0
bin/opencv_test_barcode: lib/libopencv_dnn.so.4.7.0
bin/opencv_test_barcode: 3rdparty/lib/libippiw.a
bin/opencv_test_barcode: 3rdparty/ippicv/ippicv_lnx/icv/lib/intel64/libippicv.a
bin/opencv_test_barcode: lib/libopencv_videoio.so.4.7.0
bin/opencv_test_barcode: lib/libopencv_imgcodecs.so.4.7.0
bin/opencv_test_barcode: lib/libopencv_imgproc.so.4.7.0
bin/opencv_test_barcode: lib/libopencv_core.so.4.7.0
bin/opencv_test_barcode: modules/barcode/CMakeFiles/opencv_test_barcode.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/derek/Documents/thesis-ecj/ecj/opencv/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Linking CXX executable ../../bin/opencv_test_barcode"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/barcode && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/opencv_test_barcode.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
modules/barcode/CMakeFiles/opencv_test_barcode.dir/build: bin/opencv_test_barcode
.PHONY : modules/barcode/CMakeFiles/opencv_test_barcode.dir/build

modules/barcode/CMakeFiles/opencv_test_barcode.dir/clean:
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/barcode && $(CMAKE_COMMAND) -P CMakeFiles/opencv_test_barcode.dir/cmake_clean.cmake
.PHONY : modules/barcode/CMakeFiles/opencv_test_barcode.dir/clean

modules/barcode/CMakeFiles/opencv_test_barcode.dir/depend:
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/derek/Documents/thesis-ecj/ecj/opencv /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/barcode /home/derek/Documents/thesis-ecj/ecj/opencv/build /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/barcode /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/barcode/CMakeFiles/opencv_test_barcode.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : modules/barcode/CMakeFiles/opencv_test_barcode.dir/depend

