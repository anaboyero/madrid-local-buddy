package com.madridlocalbuddy.infrastructure;

import java.util.List;

public record ResendEmailRequest(String from, List<String> to, String subject, String text) {}
