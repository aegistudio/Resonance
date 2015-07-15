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

  A DAW can be viewed as a digital signal processor. The signal flows into the DAW, the DAW do some modulation, and then flows out of the DAW (to speakers). So there're should be three main modules to handle it: input, process, output.

> +------------+   Record The Sound     +--------------------+   Process The Sound    +-------------+
> |  Input     | ---------------------> |     Process        | ---------------------> |  Output     |
> +------------+                        +--------------------+                        +-------------+

  But this division is not complete for reducing complexity. A DAW should be capable for generating sound with internal virtual instruments, process sound in mixer, and the currency of digital info-stream is complex. So we have to sub-divide the process layer. A layer called 'dataflow' is responsible for controlling how the digital information flows. Another layer called 'process' ('plugin' layer in the actual software.) provides a common interface for calling varieties of virtual instruments or effects to process.

  When the process layer works, it works rather on a physics layer. That's, we use time-value discrete space to describe sound, or we use samples to describe sound. How frequently the sound is sampled is called 'sample rate', and how precise we can describe the sound is called bit-depth. In the process layer, we only use 'double' type, or the real, to describe the sample, while the sample rate depends on the sample rate of the output device.

> +------------+   Record The Sound     +--------------------+   Process The Sound    +-------------+
> |  Input     | ---------------------> |     Data Flow      | ---------------------> |  Output     |
> +------------+                        +--------------------+                        +-------------+
>                          Determine Binary Flow | | Generate Or Process Sound
>                                       +--------------------+
>                                       |      Process       |
>                                       +--------------------+

  With these layers, the physic/acoustic flow of the sound is complete. So we all these sublayers the 'acoustic' layers, as they work on time denoted by millisecond, the frequency denoted by hertz, and the amplitude denoted by voltage. But this is not perfect!

  It's common to see DAW to use measure:beat.position with beats per minute(BPM) to denote time, the note on keyboard (do, re, mi, or actually C1, D2, etc.) to denote pitch, and the velocity and volume to denote loudness. (Be awared, I use pitch instead of frequency, and the loudness instead of amplitude.) Actually, I would like to use these instead of millisecond and hertz as they are familiar to us music producers. And we don't like to control the data flow directly in the software. We would like to see channels and tracks that are for arranging and mixing. So We add one layer called 'music' to the software. In this layer, we use nouns in music domain, and encapsulate the data flow into channels and tracks. Now it's more user friendly!

>                                       +--------------------+
>                                       |        Music       |
>                                       +--------------------+
>                                                 | Abstract And Encapsulate
> +------------+   Record The Sound     +--------------------+   Process The Sound    +-------------+
> |  Input     | ---------------------> |     Data Flow      | ---------------------> |  Output     |
> +------------+                        +--------------------+                        +-------------+
>                          Determine Binary Flow | | Generate Or Process Sound
>                                       +--------------------+
>                                       |      Process       |
>                                       +--------------------+

  The sound can't be generated without a layer to coordinate them. We would like to pass a frame into the data flow layer to get it processed, and notify music layer that the clock updates. So a 'control' layer is added to coordinates them. A coordinate layer also serve as a facade to the presentation layer. The presentation layer itself can interact with user and tell the control layer what to do, or retrieve critical information from the control layer and present them to the user.

>                                       +--------------------+
>                                       |   Presentation     |
>                                       +--------------------+
>                             Notify Interaction | | Retrieve Information
>                                       +--------------------+
>                                       |      Control       |
>                                       +--------------------+
>                                     Notify Tick | (To Both Music And Data Flow)
>                                       +--------------------+
>                                       |        Music       |
>                                       +--------------------+
>                                                 | Abstract And Encapsulate
> +------------+   Record The Sound     +--------------------+   Process The Sound    +-------------+
> |  Input     | ---------------------> |     Data Flow      | ---------------------> |  Output     |
> +------------+                        +--------------------+                        +-------------+
>                          Determine Binary Flow | | Generate Or Process Sound
>                                       +--------------------+
>                                       |      Process       |
>                                       +--------------------+

  Here we get 7 layers. There seem to be many layers, but the responsibility of each layer is actually simple:

> Input: Record The Sound, Or Format The Audio File Into Which Is Compatible With The Sound System.
> Process: Provides Application Programming Interface For Sound Processing, And Process Sound For The Upper Structures.
> Data Flow: Determine How The Data Is Flown In The Software. Render The Sound For Output.
> Output: Format The Sound Into The Format Which Is Compatible With Sound Interface Card Or Audio File.
> Music: Abstract The Timing, The Pitch And The Loundness. Encapsulate Data Flow Units Into Channels, Tracks Mixers And So On. Send Events To Plugins.
> Control: Coordinate The Work Of Lower Structure, Provide Services To Presentation.
> Presentation: Receive User Interactions, Display Critical Software Information.

