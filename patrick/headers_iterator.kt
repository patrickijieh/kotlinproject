package patrick.headers_iterator;

const val HOST: String = "Host";
const val USER_AGENT: String = "User-Agent";
const val ACCEPT: String = "Accept";
const val ACCEPT_LANGUAGE: String = "Accept-Language";
const val ACCEPT_ENCODING: String = "Accept-Encoding";
const val CONNECTION: String = "Connection";
const val CONTENT_TYPE: String = "Content-Type";
const val CONTENT_LENGTH: String = "Content-Length";

class HeadersIterator
{
    var bytes: ByteArray;
    var idx: Int;

    constructor(data: ByteArray)
    {
        this.bytes = data;
        this.idx = 0;
    }

    fun next(): Pair<Int, String>?
    {
        if (this.idx >= this.bytes.size)
            return null;
        
        var key: StringBuilder = StringBuilder();
        var value: StringBuilder = StringBuilder();

        while (this.idx < this.bytes.size && bytes[this.idx] != ':'.code.toByte())
        {
            key.append(bytes[this.idx].toInt().toChar());
            this.idx += 1;
        }

        if (key.length < 1)
            return null;
        
        this.idx += 2;

        while ( this.idx < this.bytes.size
                && bytes[this.idx] != '\n'.code.toByte())
        {
            if (bytes[idx] == '\r'.code.toByte())
            {
                this.idx += 1;
                continue;
            }
            value.append(bytes[this.idx].toInt().toChar());
            this.idx += 1;
        }

        if (value.length < 1)
            return null;
        
        this.idx += 1;
        
        return Pair(getID(key.toString()), value.toString());
    }


}

private fun getID(key: String): Int
{
    when (key)
    {
        HOST -> return 0;
        USER_AGENT -> return 1;
        ACCEPT -> return 2;
        ACCEPT_LANGUAGE -> return 3;
        ACCEPT_ENCODING -> return 4;
        CONNECTION -> return 5;
        CONTENT_TYPE -> return 6;
        CONTENT_LENGTH -> return 7;
        else -> {
            return -1;
        }
    }
}