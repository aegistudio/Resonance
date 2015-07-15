Resonance
===========
  In order to begin with, you should thoroughly read this passage, so that you could understand what is going on in this project. You're free to fork and modify the forked repository, but you should send pull request to us if you want to contribute to the project, in order to prevent underlying defects going into the repository.

Introduction
-----------
  This repository is intended to provide a cross platform music production environment for music producers, as well as a convenient application programming interface for plugin development. The project is writen in Java, but may be some native code intended for using VST architecture plugins.

  Music production consists of three phases: the pre-production phase, the production phase, and the post-production phase. In pre-production phase, producers compose music and rehearse band. In the production phase, producers record vocals and instrument performances into computer. In the post-production phase, producers arrange and mix music, master tape the album. Digital Audio Workstation (DAW) is intended for giving a hand to producers in music production.

  As a functioned DAW, the DAW should be able to coordinate the peripherals, record the sounds into the computer, provide a score edit environment to composers, a clip arrange environment to arragers, and a virtual but strong mixing board to the mixers and master-tapers. However, nearly all populated DAWs have developed for twenties or thirties of years, it's hard to alter them and we're not planning to alter them, so please don't compare this DAW to the populated DAWs.

  We welcome music lovers who are interested in developing to join us, that's why I call it 'Resonance'.

Arhitecture
-----------
  The software is divided into 7 main sub-layers. In order to comprehend them, you should read this part carefully.

  Firstly, a DAW can be viewed from a digital signal processor. The signal flows into the DAW, the DAW do some modulation, and then flows out of the DAW (to speakers). So there're should be three main modules to handle it: input, process, output.

  +------------+   Record The Sound     +--------------------+   Process The Sound    +-------------+
  |  Input     | ---------------------> |     Process        | ---------------------> |  Output     |
  +------------+                        +--------------------+                        +-------------+

  But this division is not complete for reducing complexity. A DAW should be capable for generating sound with internal virtual instruments, process sound in mixer, and the currency of digital info-stream is complex. So we have to sub-divide the process layer. A layer called 'dataflow' is responsible for controlling how the digital information flows. Another layer called 'process' ('plugin' layer in the actual software.) provides a common interface for calling varieties of virtual instruments or effects to process.

  When the process layer works, it works rather on a physics layer. That's, we use time-value discrete space to describe sound, or we use samples to describe sound. How frequently the sound is sampled is called 'sample rate', and how precise we can describe the sound is called bit-depth. In the process layer, we only use 'double' type, or the real, to describe the sample, while the sample rate depends on the sample rate of the output device.
