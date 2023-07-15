package com.orekhov.treetest.node.doc;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/documents")
public class DocumentController {
    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @PostMapping()
    public Document addDocument(@RequestBody DocumentRequest request) {
        Optional<Document> parent = documentRepository.findById(request.getParentId());
        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        if (parent.isEmpty()) {
            document.setParent(null);
        } else {
            document.setParent(parent.get());
            parent.get().getChildren().add(document);
        }
           return documentRepository.save(document);
    }

    @GetMapping("/{id}")
    public Document getDocumentHierarchy(@PathVariable Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found."));
        fetchChildren(document);
        return document;
    }

    private void fetchChildren(Document document) {
        List<Document> children = document.getChildren();
        children.forEach(this::fetchChildren);
    }
}