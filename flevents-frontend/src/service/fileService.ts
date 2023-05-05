class FileService {

  /**
   * Converts a file to a Base64-string.
   * @param file a file-object
   */
  encodeFile(file: File) {
    return new Promise<string>(function (resolve, reject) {
      let reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }

}

/**
 * Util-class for file-handling.
 * @author Ruben Kraft, David Maier
 * @since Weekly Build 4
 */
export default new FileService();
