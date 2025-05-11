import { test, expect } from '@playwright/test';

const baseUrl = 'http://localhost:4200';

test.describe('Useless Facts App Routing', () => {

  test('should redirect to /random by default', async ({ page }) => {
    await page.goto(baseUrl);
    const currentUrl = page.url();
    expect(currentUrl).toContain('/random');
  });

  test('should navigate to /random and display RandomUselessFactComponent', async ({ page }) => {
    await page.goto(`${baseUrl}/random`);
    const pageTitle = await page.locator("h1").textContent();
    expect(pageTitle).toBe('Useless Facts');
  });

  test('should navigate to /list and display AllUselessFactsComponent', async ({ page }) => {
    await page.goto(`${baseUrl}/list`);
    const pageTitle = await page.locator("h3").textContent();
    expect(pageTitle).toBe('All Useless Facts');
  });

  test('should navigate to /statistics and display UselessStatisticsComponent', async ({ page }) => {
    await page.goto(`${baseUrl}/statistics`);
    const locator = page.locator("//div[contains(@class,'bg-white p-6')]");
    await expect(locator).toBeVisible()
  });

  test('should navigate between /random and /list', async ({ page }) => {
    await page.goto(`${baseUrl}/random`);
    let pageTitle = await page.locator("h1").textContent();
    expect(pageTitle).toBe('Useless Facts');

    await page.goto(`${baseUrl}/list`);
    pageTitle = await page.locator("h3").textContent();
    expect(pageTitle).toBe('All Useless Facts');

  });

});
