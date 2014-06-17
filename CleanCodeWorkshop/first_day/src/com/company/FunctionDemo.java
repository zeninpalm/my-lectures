package com.company;

public class FunctionDemo {
    public static String testableHtml(PageData pageData,
                                      boolean includeSuiteSetup
    ) throws Exception {
        return new TestableHtmlHelper(pageData, includeSuiteSetup).invoke();
    }

    private static class TestableHtmlHelper {
        private PageData pageData;
        private boolean includeSuiteSetup;
        private WikiPage wikiPage;
        private StringBuffer buffer;

        public TestableHtmlHelper(PageData pageData, boolean includeSuiteSetup) {
            this.pageData = pageData;
            this.includeSuiteSetup = includeSuiteSetup;
            wikiPage = pageData.getWikiPage();
            buffer = new StringBuffer();
        }

        public String invoke() {
            if (isTestPage())
                includeSetups(wikiPage, buffer);
            includeContent(buffer);
            if (isTestPage())
                includeTeardowns(wikiPage, buffer);
            return finalContent();
        }

        private String finalContent() {
            return pageData.setContent(buffer.toString()).getHtml();
        }

        private void includeContent(StringBuffer buffer) {
            buffer.append(pageData.getContent()).append("\n");
        }

        private boolean isTestPage() {
            return pageData.hasAttribute("Test");
        }

        private void includeTeardowns(WikiPage wikiPage, StringBuffer buffer) {
            includeInheriteTeardown(wikiPage, buffer, "TearDown", "teardown");
            if (includeSuiteSetup) {
                includeInheriteTeardown(wikiPage, buffer, SuiteResponder.SUITE_TEARDOWN_NAME, "teardown");
            }
        }

        private void includeSetups(WikiPage wikiPage, StringBuffer buffer) {
            if (includeSuiteSetup)
                includeInheritedPage(wikiPage, buffer, SuiteResponder.SUITE_SETUP_NAME);
            includeInheritedPage(wikiPage, buffer, "SetUp");
        }

        private void includeInheriteTeardown(WikiPage wikiPage, StringBuffer buffer, String name, String mode) {
            WikiPage page =
                    PageCrawlerImpl.getInheritedPage(name, wikiPage
                    );
            if (page != null) {
                includeTags(buffer, page, mode);
            }
        }

        private void includeInheritedPage(WikiPage wikiPage, StringBuffer buffer, String pageName) {
            WikiPage suiteSetup =
                    PageCrawlerImpl.getInheritedPage(pageName, wikiPage
                    );
            if (suiteSetup != null) {
                includeTags(buffer, suiteSetup, "setup");
            }
        }

        private void includeTags(StringBuffer buffer, WikiPage page, String mode) {
            WikiPagePath pagePath = page.getPageCrawler().getFullPath(page);
            String pagePathName = PathParser.render(pagePath);
            buffer.append("!include -" + mode + " .")
                    .append(pagePathName).append("\n");
        }
    }
}
