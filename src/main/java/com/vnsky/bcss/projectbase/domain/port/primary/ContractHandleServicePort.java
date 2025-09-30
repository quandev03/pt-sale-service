package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.TypeContract;
import com.vnsky.bcss.projectbase.shared.pdf.ContractUtils;
import kotlin.Pair;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface ContractHandleServicePort {
    ByteArrayOutputStream genContractFromTemplate(InputStream templateFolder, Object data, TypeContract typeContract) throws Docx4JException, IllegalAccessException;

    ByteArrayOutputStream genContractFromTemplate(InputStream templateFolder, Object data, TypeContract typeContract, ContractUtils.TypeHandlerWord... typeHandlerWords) throws Docx4JException, IllegalAccessException;

    Pair<ByteArrayOutputStream, ByteArrayOutputStream> genContractFromTemplateForPdfAndPng(InputStream template, Object data, ContractUtils.TypeHandlerWord... typeHandlerWords);
}
