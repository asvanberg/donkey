/**
 * An opinionated and stubborn implementation of the Jakarta JSON Bind API.
 *
 * @uses jakarta.json.spi.JsonProvider
 * @provides jakarta.json.bind.spi.JsonbProvider
 */
module io.github.asvanberg.donkey {
    requires transitive jakarta.json.bind;
    requires jakarta.json;

    uses jakarta.json.spi.JsonProvider;

    provides jakarta.json.bind.spi.JsonbProvider with io.github.asvanberg.donkey.DonkeyProvider;
    exports io.github.asvanberg.donkey;
}
