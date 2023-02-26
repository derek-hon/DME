#!/usr/bin/python

import skimage
import sys

class MSSIM2:
    def compute(self, imageOne, imageTwo):
        result = skimage.metrics.structural_similarity(imageOne, imageTwo, gaussian_weights=True)
        return result.mssim


MSSIM2.compute(sys.argv[1], sys.argv[2])