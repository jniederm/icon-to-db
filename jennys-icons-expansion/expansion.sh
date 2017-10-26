#!/bin/bash

# It renames or renames original icons by UXD team to files named by guest OSes
# Usage: Put Jenny's icons to <engine-src>/packaging/icons/{large,small} and run this script in the directory

mkdir originals

[ -f windows.png ] && mv windows.png originals/windows.png
[ -f ubuntu.png ]  && mv ubuntu.png  originals/ubuntu.png
[ -f rhel.png ]  && mv rhel.png  originals/rhel.png
[ -f suse.png ]  && mv suse.png  originals/suse.png
[ -f other-unknown.png ]  && mv other-unknown.png  originals/other-unknown.png
[ -f other-linux.png ]  && mv other-linux.png  originals/other-linux.png
[ -f free_bsd.png ]  && mv free_bsd.png  originals/free_bsd.png

[ ! -L debian.png ] && mv debian.png debian_7.png
[ ! -L freebsd.png ] && ln -sr originals/free_bsd.png freebsd.png
[ ! -L freebsdx64.png ] && ln -sr originals/free_bsd.png freebsdx64.png
[ ! -L other_linux.png ] && ln -sr originals/other-linux.png other_linux.png
[ ! -L other_linux_ppc64.png ] && ln -sr originals/other-linux.png other_linux_ppc64.png
[ ! -L other.png ] && ln -sr originals/other-unknown.png other.png
[ ! -L other_ppc64.png ] && ln -sr originals/other-unknown.png other_ppc64.png
[ ! -L rhel_3.png ] && ln -sr originals/rhel.png rhel_3.png
[ ! -L rhel_3x64.png ] && ln -sr originals/rhel.png rhel_3x64.png
[ ! -L rhel_4.png ] && ln -sr originals/rhel.png rhel_4.png
[ ! -L rhel_4x64.png ] && ln -sr originals/rhel.png rhel_4x64.png
[ ! -L rhel_5.png ] && ln -sr originals/rhel.png rhel_5.png
[ ! -L rhel_5x64.png ] && ln -sr originals/rhel.png rhel_5x64.png
[ ! -L rhel_6.png ] && ln -sr originals/rhel.png rhel_6.png
[ ! -L rhel_6_ppc64.png ] && ln -sr originals/rhel.png rhel_6_ppc64.png
[ ! -L rhel_6x64.png ] && ln -sr originals/rhel.png rhel_6x64.png
[ ! -L rhel_7_ppc64.png ] && ln -sr originals/rhel.png rhel_7_ppc64.png
[ ! -L rhel_7x64.png ] && ln -sr originals/rhel.png rhel_7x64.png
[ -f rhel_atomic.png ] && mv rhel_atomic.png rhel_atomic7x64.png
[ ! -L sles_11.png ] && ln -sr originals/suse.png sles_11.png
[ ! -L sles_11_ppc64.png ] && ln -sr originals/suse.png sles_11_ppc64.png
[ ! -L ubuntu_12_04.png ] && ln -sr originals/ubuntu.png ubuntu_12_04.png
[ ! -L ubuntu_12_10.png ] && ln -sr originals/ubuntu.png ubuntu_12_10.png
[ ! -L ubuntu_13_04.png ] && ln -sr originals/ubuntu.png ubuntu_13_04.png
[ ! -L ubuntu_13_10.png ] && ln -sr originals/ubuntu.png ubuntu_13_10.png
[ ! -L ubuntu_14_04.png ] && ln -sr originals/ubuntu.png ubuntu_14_04.png
[ ! -L ubuntu_14_04_ppc64.png ] && ln -sr originals/ubuntu.png ubuntu_14_04_ppc64.png
[ ! -L windows_10.png ] && ln -sr originals/windows.png windows_10.png
[ ! -L windows_10x64.png ] && ln -sr originals/windows.png windows_10x64.png
[ ! -L windows_2003.png ] && ln -sr originals/windows.png windows_2003.png
[ ! -L windows_2003x64.png ] && ln -sr originals/windows.png windows_2003x64.png
[ ! -L windows_2008.png ] && ln -sr originals/windows.png windows_2008.png
[ ! -L windows_2008R2.png ] && ln -sr originals/windows.png windows_2008R2.png
[ ! -L windows_2008R2x64.png ] && ln -sr originals/windows.png windows_2008R2x64.png
[ ! -L windows_2008x64.png ] && ln -sr originals/windows.png windows_2008x64.png
[ ! -L windows_2012R2x64.png ] && ln -sr originals/windows.png windows_2012R2x64.png
[ ! -L windows_2012x64.png ] && ln -sr originals/windows.png windows_2012x64.png
[ ! -L windows_2016x64.png ] && ln -sr originals/windows.png windows_2016x64.png
[ ! -L windows_7.png ] && ln -sr originals/windows.png windows_7.png
[ ! -L windows_7x64.png ] && ln -sr originals/windows.png windows_7x64.png
[ ! -L windows_8.png ] && ln -sr originals/windows.png windows_8.png
[ ! -L windows_8x64.png ] && ln -sr originals/windows.png windows_8x64.png
[ ! -L windows_xp.png ] && ln -sr originals/windows.png windows_xp.png
