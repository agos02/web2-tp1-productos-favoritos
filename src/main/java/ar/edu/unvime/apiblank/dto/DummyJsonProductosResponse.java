//el envoltorio con la lista)

package ar.edu.unvime.apiblank.dto;

import java.util.List;

public record DummyJsonProductosResponse(
        List<DummyJsonProducto> products
) {
}