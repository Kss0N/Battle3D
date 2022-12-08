package se.lth.cs.student.battle3d.main


import javax.xml.parsers.{
    DocumentBuilder,
    DocumentBuilderFactory
}

import org.w3c.dom.{
    Document,
    DOMException,
    Element,
    Node,
    NodeList,
}

import java.io.{
    BufferedInputStream,
    FileNotFoundException,
    FileInputStream,
}
import se.lth.cs.student.battle3d.io.Logger

object Configs:
    var myConfigs: Map[String, String] = null

    private object Names:
        val root = "settings"

    /**  Parses the XML document into a hashmap in accordance with what's demanded.
    * 
    * 
    * As an XML document is organized into a tree like structure, it uses a depth-first (recursive) parsing algorithm. 
    * It will only be called from `configsFromFile` a lot of this code is just API-bloat.
    * @param n    current node to read
    * @param cfgs configs map into which everything is parsed
    */
    private def interpretNode(n : Element, cfgs: collection.mutable.Map[String, String]): Unit =
        val children = 
            val childNodes = n.getChildNodes()
            (0 until childNodes.getLength())
            .map(childNodes.item(_))
            .filter(_.getNodeType() == Node.ELEMENT_NODE)
            .map(_.asInstanceOf[Element])
        if children.length > 0 then
            for toRead <- children do
                interpretNode(toRead, cfgs)
        else 
            val value = n.getTextContent()
            val name : String =
                //Ignore the root node as prefix.
                if  n.getParentNode != null && 
                    n.getParentNode().getNodeName() != Names.root 
                then
                    n.getParentNode().getNodeName() + "." + n.getNodeName()
                else 
                    n.getNodeName()
            if cfgs.contains(name) then 
                cfgs.update(name, value)
            else
                cfgs += ((name, value))

    def fromFile(filePath: String = "src/rsc/cfgs/default.xml"): Map[String, String] = 
        var inputStream: BufferedInputStream = null
        val cfgs = collection.mutable.Map.empty[String, String]
        try
            val fileStream = FileInputStream(filePath)
            inputStream = BufferedInputStream(fileStream)
            val documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val document = documentBuilder.parse(inputStream)
            val root = document.getDocumentElement()
            interpretNode(root, cfgs)
        catch
            case e: FileNotFoundException =>
                Logger.printFatal("File not found exception:" + filePath)
                Map.empty[String, String]
            case e: DOMException =>
                Logger.printFatal("DOM EXCEPTION :" + e.getMessage())
                Map.empty[String, String]
        finally
            if inputStream != null then
                inputStream.close()
        cfgs.toMap

    def apply(key: String): String = 
        if myConfigs.contains(key) then 
            myConfigs(key)
        else
            Logger.printError(s"$key is not a valid config")
            ""
