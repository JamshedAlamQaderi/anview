package com.jamshedalamqaderi.anview.exceptions

class AnViewObserverAlreadyRegisteredException(tag: String) :
    Exception("Tag('$tag') already registered in AnViewObserver list")
