package com.vnsky.bcss.projectbase.domain.service;

import com.spire.doc.Document;
import com.vnsky.bcss.projectbase.domain.port.primary.ContractHandleServicePort;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.TypeContract;
import com.vnsky.bcss.projectbase.shared.pdf.ContractUtils;
import com.vnsky.common.exception.domain.BaseException;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractHandleService implements ContractHandleServicePort {

    @Override
    public ByteArrayOutputStream genContractFromTemplate(InputStream template, Object data, TypeContract typeContract) {
        return genContractFromTemplate(template, data, typeContract, ContractUtils.TypeHandlerWord.MAIL_MERGE);
    }

    @Override
    public ByteArrayOutputStream genContractFromTemplate(InputStream template, Object data, TypeContract typeContract, ContractUtils.TypeHandlerWord... typeHandlerWords) {
        try {
            if (template == null) {
                log.error("Template not found");
                throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                    .addParameter(Constant.Error.MESSAGE, Constant.Error.CUS00033_01)
                    .build();
            }
            log.info("template inputstream with size  : {} ", template.available());
            // WordProcess with inputStream
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.load(template);
            log.info("load template word to word process ml package");
            // mergeField
            log.info("mail merge data to word");
            if(typeHandlerWords != null){
                for (ContractUtils.TypeHandlerWord typeHandlerWord : typeHandlerWords) {
                    if(ContractUtils.getHandlerWordMap().containsKey(typeHandlerWord))
                        ContractUtils.getHandlerWordMap().get(typeHandlerWord).accept(wordprocessingMLPackage, data);
                }
            }
            ByteArrayOutputStream outputStreamDocx = new ByteArrayOutputStream();
            wordprocessingMLPackage.save(outputStreamDocx);
            // merge field with spire
            // convert word to pdf data
            return ContractUtils.convertWordToPdfByteArrayOutputStream(new ByteArrayInputStream(outputStreamDocx.toByteArray()), typeContract);
        } catch (Exception e) {
            log.error("Exception when gen contract", e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL,e)
                .addParameter(Constant.Error.MESSAGE, e.getMessage())
                .build();
        }
    }

    /**
     *
     * @param template: File hợp đồng
     * @param data: Dữ liệu fill vào hợp đồng
     * @param typeHandlerWords: Các loại cần xử lý tài liệu
     * @return 1 Pair, gồm first là dữ lịệu file pdf, second là dữ liệu file png
     */
    @Override
    public Pair<ByteArrayOutputStream, ByteArrayOutputStream> genContractFromTemplateForPdfAndPng(InputStream template, Object data, ContractUtils.TypeHandlerWord... typeHandlerWords) {
        try {
            if (template == null) {
                log.error("Template not found");
                throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                    .addParameter(Constant.Error.MESSAGE, Constant.Error.CUS00033_01)
                    .build();
            }
            log.info("template inputstream with size  : {} ", template.available());
            // WordProcess with inputStream
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.load(template);
            log.info("load template word to word process ml package");
            // mergeField
            log.info("mail merge data to word");
            if(typeHandlerWords != null){
                for (ContractUtils.TypeHandlerWord typeHandlerWord : typeHandlerWords) {
                    if(ContractUtils.getHandlerWordMap().containsKey(typeHandlerWord))
                        ContractUtils.getHandlerWordMap().get(typeHandlerWord).accept(wordprocessingMLPackage, data);
                }
            }
            ByteArrayOutputStream outputStreamDocx = new ByteArrayOutputStream();
            wordprocessingMLPackage.save(outputStreamDocx);
            // merge field with spire
            // convert word to pdf data

            InputStream stream = new ByteArrayInputStream(outputStreamDocx.toByteArray());
            Document document = new Document(stream);
            return ContractUtils.convertWordToPdfAndPng(document);
        } catch (Exception e) {
            log.error("Exception when gen contract", e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL,e)
                .addParameter(Constant.Error.MESSAGE, e.getMessage())
                .build();
        }
    }
}
