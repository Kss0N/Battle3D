package se.cs.lth.student.IO.Logger


import java.io.FileOutputStream
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.IOException

import java.util.Date
import java.util.Calendar

object Logger:

    /**Severity of the message
     * 
     * uses ANSI codes for coloring
     * (see https://gist.github.com/mgumiero9/665ab5f0e5e7e46cb049c1544a00e29f and https://en.wikipedia.org/wiki/ANSI_escape_code#8-bit)
    */
    enum Severity(val colorCode: String):
        case INFO   extends Severity("\u001B[34m")  //Blue
        case DEBUG  extends Severity("\u001B[32m")  //Green
        case WARN   extends Severity("\u001B[33m")  //Yellow
        case ERROR  extends Severity("\u001B[232m") //Orange
        case FATAL  extends Severity("\u001B[31m")  //Red

    private object Severity:
        val reset   = "\u001B[0m"   //reset ansi state
        val bold    = "\u001B[1m"   //same as html `<b>`
        val unbold  = "\u001B[21m"  //same as html `<\b>`

    //Comfortability items:

    private var myLogger: Logger = null

    //Called first
    def init(path: String)  : Logger = 
        myLogger = new Logger(path)
        myLogger

    def apply(path: String): Logger = init(path)

    //Called last
    def destroy()           : Unit = myLogger.destroy()

    //May be called in the middle; not before init and not after destroy
    def reset(newPath: String): String =
        val oldPath = myLogger.filePath
        myLogger.destroy()
        myLogger = new Logger(newPath)
        oldPath
    
    //Used for logging purposes not pertaining to debugging or errors
    def printInfo   (msg: String)   = myLogger.newEntry(Severity.INFO, msg)
    //Used for debugging purposes
    def printDebug  (msg: String)   = myLogger.newEntry(Severity.DEBUG, msg)
    //Used for problems that are not currently problems, but may be in the future
    def printWarn   (msg: String)   = myLogger.newEntry(Severity.WARN, msg)
    //Used for errors that still may be recovered while in the development phase, but should probably cause crash in production code
    def printError  (msg: String)   = myLogger.newEntry(Severity.ERROR, msg)
    //Used for unrecoverable errors (ex. any java.lang.error)
    def printFatal  (msg: String)   = myLogger.newEntry(Severity.FATAL, msg)

    


/** Logging tool for logging and debugging
 * 
*/
case class Logger (private val filePath: String = "battle3d.log"):
    import Logger.Severity

    private val appendedPath = 
        if filePath.contains(".log") then filePath
        else filePath + ".log"
    private val file        = new FileOutputStream(appendedPath)
    private val outWriter   = new OutputStreamWriter(file)      
    private val bufWriter   = new BufferedWriter(outWriter) //Java doesn't assume you want your output stream served with a buffer

    private val calendar = Calendar.getInstance()    


    /** Closes the file handle
     * 
     * Should only be called by a cleanup procedure or a singleton comfortability procedure (see companion object) called when a forceful exit is triggered
    */
    def destroy(): Unit = 
        try
            bufWriter.flush()
            bufWriter.close()
        catch
            case e: IOException => 
                println(e.getMessage())

    /** Adds a new entry into the file
     * 
     * entry follows the following syntax:
     * <code> [YY.MM.DD] HH.MM.SS $Severity : $msg </code>
     * 
     * if severity is set to Logger.Severity.FATAL then this will call a forceful exit, as it cannot be recovered from
    */
    def newEntry(severity: Logger.Severity, msg: String): Unit = 
        val year    = this.calendar.get(Calendar.YEAR) % 100 //Only want the last two digits because everyone may assume that we're in the 21st century
        val month   = this.calendar.get(Calendar.MONTH)
        val day     = this.calendar.get(Calendar.DAY_OF_MONTH)

        val hour    = this.calendar.get(Calendar.HOUR)
        val minute  = this.calendar.get(Calendar.MINUTE)
        val second  = this.calendar.get(Calendar.SECOND)

        val header = s"[${year}.${month}.${day}] $hour.$minute.$second ${Severity.bold}${severity.toString()}${Severity.unbold}"
        val myText = severity.colorCode + msg + Severity.reset
        try 
            bufWriter.write(header, 0, header.length())
            bufWriter.write(myText, 0, myText.length())
            bufWriter.newLine()
        catch
            case e: IOException =>
                //TODO: add handling for failure to log