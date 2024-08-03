# Card Generator

Card Generator allows you to create any card and generate it in JPG, JPEG or PNG format. It also allows you to download images in PDF mode.

## Supported languages
- Slovenian
- English

## What do you need?

- **Excel file** <a href="https://github.com/pintargasper/CardGenerator/releases/download/v1.0.3/Cards.xlsx" target="_blank">`Cards.xlsx`</a> (Sample file) -> obtained from GitHub page
- **Pictures folder** <a href="https://github.com/pintargasper/CardGenerator/releases/download/v1.0.3/images.zip" target="_blank">`images`</a> (Sample folder) -> obtained from GitHub page
- **File (Template)** -> obtained from the <a href="https://cards.gasperpintar.com/create" target="_blank">`Template`</a>

## Supported formats

- **A4** -> 3 x 3 images to one page. Card Size: Width: 240px; Height: 332px
- **13x18** -> 2 x 2 images to one page. Card Size: Width: 240px; Height: 332px

## Currently additional components

### LoadingBar component

The LoadingBar component enables the display of a loading bar.

#### Using the LoadingBar component

```jsx
<LoadingBar 
    title={"Title"}
    progress={Number}
    textColor={"Text color"} 
    loadingBarColor={"Loading bar color"} 
    fontFamily={"Font family"}
    fontSize={Text size} 
/>
