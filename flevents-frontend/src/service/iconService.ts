const suffixIconMap = new Map<RegExp, string>([

  // General
  [/(png|jpg|jpeg|webp|gif|avif|svg)$/, 'mdi-file-image'],
  [/(mp4|mov|wmv|flv|avi)$/, 'mdi-file-video'],
  [/(mp3|wav|ogg|m4a|flac)$/, 'mdi-file-music'],
  [/(xml|json|yml|c|cpp|cs|html|htm|php|css|scss|js|ts|vue|hs|java|pl|tex|bib|cmd|bat|md)$/, 'mdi-file-code'],
  [/(csv|ssv|tsv)$/, 'mdi-file-delimited'],
  [/(txt)$/, 'mdi-file-document'],

  // PDF
  [/(pdf)$/, 'mdi-file'],

  // OpenDocument
  [/(odt)$/, 'mdi-file-document'],
  [/(ods)$/, 'mdi-file-table'],
  [/(odp)$/, 'mdi-file'],
  [/(odg)$/, 'mdi-file-image'],

  // Office
  [/(doc|docx)$/, 'mdi-file-word'],
  [/(xls|xlsx)$/, 'mdi-file-excel'],
  [/(ppt|pptx)$/, 'mdi-file-powerpoint'],

  // Archives
  [/(zip|7zip|7z|gz)$/, 'mdi-folder-zip'],

  // Executables
  [/(exe|dll|jar|iso)$/, 'mdi-file'],
]);

/**
 * Util-Class for global icon-control.
 * @author David Maier
 * @since Weekly Build 4
 */
class IconService {
  /**
   * Determines the matching Material-Design-Icon for the given file name.
   * @param filename the filename including the extension
   * @returns a mdi-icon-string name
   */
  getIconForFileType(filename: string) {
    let suffix = filename.split('.').at(-1)?.toLowerCase() ?? '';
    for (let [regex, icon] of suffixIconMap) {
      if (suffix.match(regex)) {
        return icon;
      }
    }
    return 'mdi-file';
  }
}

export default new IconService();
