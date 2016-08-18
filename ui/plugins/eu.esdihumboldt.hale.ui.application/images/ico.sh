#!/bin/sh
#
# Convert .pngs to an .ico that can be used with Tycho.
# 
# ImageMagick documentation see
# - http://www.imagemagick.org/Usage/formats/#bmp
# - http://www.imagemagick.org/script/command-line-options.php#compress

# 8-bit images

# XXX only two out of three accepted by Tycho
#convert 16.png ppm:- | convert - -colors 256 -compress None BMP3:16_8bit.bmp
#convert 32.png ppm:- | convert - -colors 256 -compress None BMP3:32_8bit.bmp
#convert 48.png ppm:- | convert - -colors 256 -compress None BMP3:48_8bit.bmp

# these images were now created with Gimp instead: Indexed mode (web palette)

# 32-bit images (not accepted by Tycho if supplied as .bmp, but in the .ico they seem to work)

convert 16.png -compress None 16.bmp
convert 32.png -compress None 32.bmp
convert 48.png -compress None 48.bmp
convert 256.png -compress None 256.bmp

# Create an icon (using no compressions seems to be very important)
convert 16.bmp 32.bmp 48.bmp 256.bmp \
  16_8bit.bmp 32_8bit.bmp 48_8bit.bmp -compress None hale.ico