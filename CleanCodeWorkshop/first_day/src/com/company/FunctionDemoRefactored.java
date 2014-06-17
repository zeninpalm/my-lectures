package com.company;

public class FunctionDemoRefactored {
    public static String testableHtml(PageData pageData,
                                      boolean includeSuiteSetup
    ) throws Exception {
        return new TestableHtmlHelper(pageData, includeSuiteSetup).invoke();
    }

    private static class TestableHtmlHelper {
        private PageData pageData;
        private boolean includeSuiteSetup;
        private WikiPage wikiPage;
        private final StringBuffer buffer;

        public TestableHtmlHelper(PageData pageData, boolean includeSuiteSetup) {
            this.pageData = pageData;
            this.includeSuiteSetup = includeSuiteSetup;
            wikiPage = pageData.getWikiPage();
            buffer = new StringBuffer();
        }

        public String invoke() {
            if (isTestPage())
                includeSetups();
            buffer.append(pageData.getContent()).append("\n");
            if (isTestPage())
                includeTeardowns();
            setPageContent();
            return pageData.getHtml();
        }

        private void setPageContent() {
            pageData.setContent(buffer.toString());
        }

        private boolean isTestPage() {
            return pageData.hasAttribute("Test");
        }

        private void includeTeardowns() {
            includeIfInherited(mode, "TearDown");
            if (includeSuiteSetup)
                includeIfInherited("TearDown", SuiteResponder.SUITE_TEARDOWN_NAME);
        }

        private void includeSetups() {
            if (includeSuiteSetup)
                includeIfInherited("setup", SuiteResponder.SUITE_SETUP_NAME);
            includeIfInherited(mode, "SetUp");
        }

        private void includeIfInherited(String mode, String pageName) {
            WikiPage page =
                    PageCrawlerImpl.getInheritedPage(pageName, wikiPage
                    );
            if (page != null) {
                includeTags(mode, page);
            }
        }

        private void includeTags(String mode, WikiPage setup) {
            WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(setup);
            String pagePathName = PathParser.render(pagePath);
            buffer.append("!include -" + mode + " .")
                    .append(pagePathName).append("\n");
        }
    }
}
